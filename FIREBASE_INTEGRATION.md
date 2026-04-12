# Firebase Integration Guide for Back2Owner

This guide shows how to use Firebase services in the Back2Owner application.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     UI Layer (Compose)                      │
│              (Screens, ViewModels, State)                   │
├─────────────────────────────────────────────────────────────┤
│                Domain Layer (Use Cases)                     │
│            (Business Logic, Flow & Coroutines)             │
├─────────────────────────────────────────────────────────────┤
│            Data Layer (Repositories)                    │
│    (Firebase, Firestore, Storage, Authentication)         │
├─────────────────────────────────────────────────────────────┤
│         Firebase Services Layer                            │
│  (Auth, Firestore, Storage, Messaging, Config)             │
└─────────────────────────────────────────────────────────────┘
```

## Firebase Modules

### 1. FirebaseConfig

**Location:** `firebase/FirebaseConfig.kt`

Handles general Firebase initialization and configuration.

**Available Functions:**

```kotlin
// Verify Firebase connection
val isConnected = FirebaseConfig.verifyFirebaseConnection()

// Get authentication status
val authStatus = FirebaseConfig.getAuthStatus()
when (authStatus) {
    is AuthStatus.Authenticated -> Log.d("Firebase", "User: ${authStatus.userId}")
    AuthStatus.Unauthenticated -> Log.d("Firebase", "No user logged in")
    AuthStatus.Unknown -> Log.d("Firebase", "Status unknown")
}

// Initialize FCM
FirebaseConfig.initializeFCM()

// Control offline persistence
FirebaseConfig.enableOfflinePersistence()
FirebaseConfig.disableOfflinePersistence()
```

### 2. FCMConfig

**Location:** `firebase/FCMConfig.kt`

Manages Firebase Cloud Messaging and push notifications.

**Available Functions:**

```kotlin
// Get FCM device token
val token = FCMConfig.getDeviceToken()

// Subscribe to topic for notifications
FCMConfig.subscribeToTopic(FCMTopics.ITEM_MATCH_ALERTS)
FCMConfig.subscribeToTopic(FCMTopics.USER_NOTIFICATIONS)

// Unsubscribe from topic
FCMConfig.unsubscribeFromTopic(FCMTopics.ITEM_MATCH_ALERTS)
```

**Available Topics:**

```kotlin
FCMTopics.ITEM_MATCH_ALERTS      // For item matching notifications
FCMTopics.USER_NOTIFICATIONS     // For user-specific notifications
FCMTopics.CLAIM_UPDATES          // For claim status updates
FCMTopics.GENERAL_ANNOUNCEMENTS  // For general announcements
```

## Repository Usage Examples

### Authentication Repository

**Injecting the repository:**

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    // Use userRepository in your logic
}
```

**Common operations:**

```kotlin
// Create user
viewModelScope.launch {
    val result = userRepository.createUser(User(/* ... */))
    result.onSuccess { userId ->
        Log.d("Auth", "User created: $userId")
    }
    result.onFailure { exception ->
        Log.e("Auth", "Error creating user", exception)
    }
}

// Get current user
val currentUser = userRepository.getCurrentUser()

// Sign out
userRepository.signOut()
```

### Item Repository

**Injecting the repository:**

```kotlin
@HiltViewModel
class ItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    // Use itemRepository in your logic
}
```

**Common operations:**

```kotlin
// Report a lost/found item
viewModelScope.launch {
    val item = Item(
        id = UUID.randomUUID().toString(),
        title = "Lost Keys",
        description = "Red keychain with metal ring",
        itemType = "lost",
        category = "keys",
        location = "Library",
        reporterID = currentUserId,
        timestamp = System.currentTimeMillis(),
    )
    
    val result = itemRepository.reportItem(item)
    result.onSuccess { itemId ->
        Log.d("Item", "Item reported: $itemId")
    }
}

// Get lost items
viewModelScope.launch {
    val result = itemRepository.getLostItems(mapOf("category" to "keys"))
    result.onSuccess { items ->
        // Update UI with items
    }
}

// Search for items
itemRepository.searchItems("lost keys", "lost").collect { items ->
    // Update UI with search results
}

// Upload item photo
viewModelScope.launch {
    val result = itemRepository.uploadItemPhoto(itemId, photoBytes)
    result.onSuccess { photoUrl ->
        Log.d("Storage", "Photo uploaded: $photoUrl")
    }
}
```

### Claim Repository

**Injecting the repository:**

```kotlin
@HiltViewModel
class ClaimViewModel @Inject constructor(
    private val claimRepository: ClaimRepository,
) : ViewModel() {
    // Use claimRepository in your logic
}
```

**Common operations:**

```kotlin
// Create a claim request
viewModelScope.launch {
    val claim = ClaimRequest(
        id = UUID.randomUUID().toString(),
        itemId = itemId,
        claimantId = currentUserId,
        answers = mapOf("color" to "blue", "brand" to "Sony"),
        status = "pending",
        createdAt = System.currentTimeMillis(),
    )
    
    val result = claimRepository.createClaim(claim)
    result.onSuccess { claimId ->
        Log.d("Claim", "Claim created: $claimId")
    }
}

// Listen to claims for an item
claimRepository.getClaimsForItem(itemId).collect { claims ->
    // Update UI with claim requests
}
```

## Data Models

### User
```kotlin
data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val profilePhotoUrl: String? = null,
    val rating: Float = 0f,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
)
```

### Item
```kotlin
data class Item(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val itemType: String = "", // "lost" or "found"
    val category: String = "",
    val location: String = "",
    val photoUrls: List<String> = emptyList(),
    val reporterID: String = "",
    val status: String = "active", // "active", "claimed", "resolved"
    val timestamp: Long = 0,
)
```

### ClaimRequest
```kotlin
data class ClaimRequest(
    val id: String = "",
    val itemId: String = "",
    val claimantId: String = "",
    val answers: Map<String, String> = emptyMap(),
    val status: String = "pending", // "pending", "verified", "rejected"
    val createdAt: Long = 0,
)
```

## Best Practices

### 1. Use ViewModels for Firebase Operations

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.getData().collect { data ->
                _uiState.value = UiState.Success(data)
            }
        }
    }
}
```

### 2. Handle Results Properly

```kotlin
viewModelScope.launch {
    val result = repository.performOperation()
    result.onSuccess { data ->
        // Handle success
    }
    result.onFailure { exception ->
        // Handle error
        Log.e("Error", exception.message, exception)
    }
}
```

### 3. Use Flows for Real-Time Updates

```kotlin
repository.observeItems().collect { items ->
    _uiState.value = UiState.Success(items)
}
```

### 4. Cancel Operations with Scope

```kotlin
viewModelScope.launch {
    // Automatically cancelled when ViewModel is destroyed
    repository.observeData().collect { /* ... */ }
}
```

### 5. Error Handling

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success<T>(val data: T) : UiState()
    data class Error(val message: String) : UiState()
}
```

## Firebase Security Rules Quick Reference

### Firestore Rules Structure
- Users can only read/write their own user document
- Items are readable if status != 'DRAFT' or user is the reporter
- Claims can only be created by authenticated users
- Items can only be updated by the reporter

### Storage Rules Structure
- Item photos: anyone can read, authenticated users can write (10MB max)
- User photos: anyone can read, only owner can write (5MB max)

## Troubleshooting

### Common Issues

**Issue: Firestore operations timeout**
- Solution: Ensure internet connection is active
- Check Firebase rules allow your operations
- Verify user is authenticated

**Issue: Storage upload fails**
- Solution: Check file size doesn't exceed limits
- Verify user is authenticated
- Ensure storage rules allow write access

**Issue: Offline mode not working**
- Solution: Verify offline persistence is enabled in Back2OwnerApp.kt
- Check Firestore has cached data before going offline

**Issue: FCM tokens not updating**
- Solution: Ensure Firebase Cloud Messaging service is running
- Check AndroidManifest.xml includes messaging service
- Verify FCM topic subscriptions

## Next Steps

1. ✅ Deploy security rules (see FIREBASE_SETUP.md)
2. ✅ Implement authentication screens
3. ✅ Create repositories for your data models
4. ✅ Add ViewModels that use repositories
5. ✅ Implement UI screens with Compose
6. ✅ Test with Firebase Emulator (optional)
7. ✅ Set up backend Cloud Functions (optional)

## Resources

- [Firebase Kotlin Documentation](https://firebase.google.com/docs/android)
- [Firestore Query Reference](https://firebase.google.com/docs/firestore/query-data/get-data)
- [Firebase Storage Guide](https://firebase.google.com/docs/storage/android/start)
- [FCM Documentation](https://firebase.google.com/docs/cloud-messaging)

# Back2Owner - Implementation Guide

This guide provides detailed implementation instructions for the three core "Win" features of the Back2Owner application.

---

## Feature 1: Verification Gate with Blurred Photos

### Overview
The Verification Gate ensures that only the actual item owner can access the contact details of the person who found their item. This is done through a combination of photo blurring and security question verification.

### Implementation Steps

#### Step 1: Photo Blurring Service
Create a service to blur item photos before displaying them in the feed:

```kotlin
// In UI layer, before displaying photos in feed
private suspend fun createBlurredImage(photoBytes: ByteArray): ByteArray {
    // Use Android Graphics APIs or a library like Blur (by Elytradev)
    // Recommended: Use RenderScript or Glide transformation
    // This is computationally done server-side for security
    return blurPhotoOnServer(photoBytes)
}
```

#### Step 2: Security Question Setup (Report Item Screen)
```kotlin
// In ReportItemScreen
OutlinedTextField(
    value = securityQuestion,
    onValueChange = { securityQuestion = it },
    label = { Text("Security Question") },
    placeholder = { Text("e.g., What color is the phone case?") }
)

OutlinedTextField(
    value = securityAnswer,
    onValueChange = { securityAnswer = it },
    label = { Text("Answer") },
    placeholder = { Text("Answer (will be hashed)") }
)
```

#### Step 3: Hash the Security Answer
```kotlin
// In FirebaseItemRepository
private fun hashSecurityAnswer(answer: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val hashBytes = md.digest(answer.toByteArray())
    return Base64.encodeToString(hashBytes, Base64.DEFAULT)
}

// Store only the hash
item.copy(securityAnswerHash = hashSecurityAnswer(answer))
```

#### Step 4: Claim Verification
```kotlin
// In CreateClaimUseCase
suspend operator fun invoke(itemId: String, claimerId: String, securityAnswer: String) {
    val item = itemRepository.getItemById(itemId).getOrThrow()
    val providedAnswerHash = hashSecurityAnswer(securityAnswer)
    
    if (providedAnswerHash != item.securityAnswerHash) {
        throw Exception("Incorrect security answer")
    }
    
    // Create claim and reveal contact details
    val claim = ClaimRequest(
        itemID = itemId,
        claimerID = claimerId,
        isVerified = true
    )
    claimRepository.createClaimRequest(claim)
}
```

#### Step 5: Display Logic in UI
```kotlin
@Composable
fun ItemCard(item: Item) {
    val isVerifiedUser = false // TODO: Check if current user is owner
    
    AsyncImage(
        model = if (isVerifiedUser) item.photoURL else item.blurredPhotoURL,
        contentDescription = "Item photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(80.dp)
    )
    
    if (!isVerifiedUser) {
        Text("Answer the security question to see details", fontStyle = FontStyle.Italic)
    }
}
```

---

## Feature 2: Type-Safe Category System

### Overview
Using Kotlin's sealed class feature to create a type-safe category system that prevents invalid values and makes the code more maintainable.

### Implementation

#### Step 1: Define the Sealed Class
```kotlin
// In data/model/Item.kt
sealed class ItemCategory(val name: String, val displayName: String, val icon: String) {
    object Electronics : ItemCategory("electronics", "Electronics", "📱")
    object Documents : ItemCategory("documents", "ID & Cards", "📄")
    object Stationery : ItemCategory("stationery", "Books & Pens", "✏️")
    object Personal : ItemCategory("personal", "Wallets & Keys", "🔑")
    object Clothing : ItemCategory("clothing", "Clothing & Accessories", "👕")
    object Other : ItemCategory("other", "Other Items", "📦")

    companion object {
        fun fromString(value: String): ItemCategory = when (value) {
            "electronics" -> Electronics
            "documents" -> Documents
            "stationery" -> Stationery
            "personal" -> Personal
            "clothing" -> Clothing
            else -> Other
        }

        fun getAllCategories(): List<ItemCategory> = listOf(
            Electronics, Documents, Stationery, Personal, Clothing, Other
        )
        
        fun getIconForCategory(category: ItemCategory): String = category.icon
    }
}
```

#### Step 2: Use in Filtering
```kotlin
// In FeedViewModel
fun filterByCategory(category: ItemCategory?) {
    val filteredItems = if (category == null) {
        allItems
    } else {
        allItems.filter { item ->
            ItemCategory.fromString(item.category) == category
        }
    }
}
```

#### Step 3: UI Category Selection
```kotlin
@Composable
fun CategoryFilterRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ItemCategory.getAllCategories().forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(category.icon)
                        Text(category.displayName)
                    }
                }
            )
        }
    }
}
```

#### Step 4: Type Safety Benefits
```kotlin
// ✅ GOOD: Compiler prevents invalid values
val category: ItemCategory = ItemCategory.Electronics
val items = getItemsByCategory(category)

// ❌ BAD: Would be caught at compile-time if using sealed class
val invalidCategory = "invalid-category" // This is a String, not ItemCategory
```

---

## Feature 3: Real-time Notifications with FCM

### Overview
Firebase Cloud Messaging (FCM) sends instant notifications to users when a matching item is posted or when there's a claim update.

### Implementation

#### Step 1: Set Up FCM Service
```kotlin
// services/Back2OwnerMessagingService.kt
@AndroidEntryPoint
class Back2OwnerMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"] ?: "Back2Owner"
        val message = remoteMessage.data["message"] ?: ""
        val itemId = remoteMessage.data["itemId"]
        
        showNotification(title, message, itemId)
        
        // Save notification to Firestore for history
        saveNotificationToFirestore(title, message, itemId)
    }

    override fun onNewToken(token: String) {
        // Update FCM token in Firestore
        sendTokenToBackend(token)
    }

    private fun showNotification(title: String, message: String, itemId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            itemId?.let { putExtra("itemId", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), notification)
    }
}
```

#### Step 2: Trigger Notifications from Backend
Use Cloud Functions (in Firebase console):

```javascript
// Cloud Function to trigger when item is posted
exports.sendItemMatchNotification = functions.firestore
    .document('items/{itemId}')
    .onCreate(async (snap, context) => {
        const item = snap.data();
        
        // Find users who have saved searches matching this item
        const matchingUsers = await findMatchingUsers(item);
        
        for (const user of matchingUsers) {
            await sendNotification(user.uid, {
                title: `Found: ${item.title}!`,
                message: `A ${item.category} matching your search was posted`,
                itemId: snap.id,
                type: 'item_match'
            });
        }
    });

function sendNotification(uid, payload) {
    return admin.messaging().sendToDevice(userTokens[uid], {
        notification: {
            title: payload.title,
            body: payload.message
        },
        data: payload
    });
}
```

#### Step 3: Request Notification Permissions (Android 13+)
```kotlin
// In MainActivity.kt
val notificationPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Permission granted, notifications will show
    }
}

LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
```

#### Step 4: Save FCM Token to Firestore
```kotlin
// In AuthRepository or on first launch
suspend fun updateFCMToken(uid: String) {
    val token = FirebaseMessaging.getInstance().token.await()
    firestore.collection("users").document(uid)
        .update("fcmTokens", FieldValue.arrayUnion(token))
        .await()
}
```

#### Step 5: View Notification History
```kotlin
@Composable
fun NotificationsScreen(viewModel: NotificationViewModel = hiltViewModel()) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    
    LazyColumn {
        items(notifications) { notification ->
            NotificationItem(notification) {
                // Navigate to item detail
                navController.navigate("item/${notification.relatedItemId}")
            }
        }
    }
}
```

---

## Implementation Timeline

### Quick Start (2-3 hours)
- [ ] Implement Feature 2 (Categories) - Type-safe system
- [ ] Link category filtering to Firestore queries
- [ ] Add category chips to UI

### Next Phase (4-5 hours)
- [ ] Implement Feature 1 (Verification Gate)
- [ ] Add photo blurring to Firebase Storage
- [ ] Create security answer hashing logic
- [ ] Build claim verification UI

### Final Phase (3-4 hours)
- [ ] Set up Firebase Cloud Functions
- [ ] Implement Feature 3 (FCM Notifications)
- [ ] Test end-to-end notification flow
- [ ] Handle notification taps

---

## Testing Strategy

### Unit Tests
```kotlin
// Test category conversion
@Test
fun testCategoryFromString() {
    val category = ItemCategory.fromString("electronics")
    assertEquals(ItemCategory.Electronics, category)
}

// Test security answer hashing
@Test
fun testSecurityAnswerHashing() {
    val answer = "Red"
    val hash1 = hashAnswer(answer)
    val hash2 = hashAnswer(answer)
    assertEquals(hash1, hash2) // Deterministic hashing
}
```

### Integration Tests
- [ ] Test item upload with security question
- [ ] Test claim creation and verification
- [ ] Test notification delivery
- [ ] Test category filtering with real Firestore data

---

## Common Issues and Solutions

| Issue | Solution |
|-------|----------|
| Photos not blurring | Ensure cloud function runs before storing URL |
| Notifications not showing | Check FCM token is updated; verify notification channel |
| Security question mismatch | Ensure consistent salting/hashing; case-sensitivity |
| Category filtering returns empty | Check Firestore indexes are created for category queries |

---

## Performance Tips

1. **Pagination:** Load items in batches of 50
2. **Caching:** Use Coil for image caching
3. **Lazy Loading:** Implement lazy lists for large feeds
4. **Throttling:** Debounce search queries (300ms delay)
5. **Indexing:** Create Firestore composite indexes for common filters

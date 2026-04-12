# Firebase Quick Start Guide

Get the Back2Owner Firebase backend running in 5 minutes!

## Prerequisites

- Android Studio installed
- Firebase CLI installed (`npm install -g firebase-tools`)
- Access to Back2Owner Firebase project

## 🚀 Step 1: Deploy Security Rules (2 minutes)

This is the most important step to enable Firestore and Storage operations.

```bash
# Navigate to project root
cd c:\Users\harsh\Back2Owner

# Login to Firebase
firebase login

# Deploy rules
firebase deploy --only firestore:rules,storage
```

✅ Security rules are now live!

## 🏗️ Step 2: Build the App (2 minutes)

```bash
# From project root
./gradlew clean build
./gradlew installDebug
```

✅ App is built and installed!

## ▶️ Step 3: Run and Verify (1 minute)

1. **Connect Android device or emulator**
2. **Run the app:** `./gradlew runDebug`
3. **Check Logcat for success messages:**
   ```
   D/Back2OwnerApp: Firebase initialized successfully
   D/Back2OwnerApp: Firestore offline persistence enabled
   ```

✅ Firebase is connected!

---

## 📚 What's Already Configured?

- ✅ Firebase dependencies
- ✅ Google Services plugin
- ✅ Hilt dependency injection
- ✅ Firebase repositories
- ✅ Firestore offline persistence
- ✅ Cloud Messaging service
- ✅ Security rules
- ✅ Storage rules
- ✅ Firestore indexes

## 🔧 Firebase Services Available

| Service | Location | Ready to Use |
|---------|----------|--------------|
| Authentication | `RepositoryModule.kt` | ✅ Provided via DI |
| Firestore | `RepositoryModule.kt` | ✅ Provided via DI |
| Storage | `RepositoryModule.kt` | ✅ Provided via DI |
| Cloud Messaging | `Back2OwnerMessagingService.kt` | ✅ Configured |
| Configuration | `firebase/FirebaseConfig.kt` | ✅ Ready |
| FCM Management | `firebase/FCMConfig.kt` | ✅ Ready |

## 💡 How to Use Firebase in Your Code

### Inject a Repository

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    // Use itemRepository here
}
```

### Perform Operations

```kotlin
// Report an item
viewModelScope.launch {
    val result = itemRepository.reportItem(item)
    result.onSuccess { itemId -> 
        // Item reported successfully
    }
    result.onFailure { exception ->
        // Handle error
    }
}

// Search for items
itemRepository.searchItems("lost keys", "lost").collect { items ->
    // Update UI with results
}
```

## 🧪 Test Firebase Connection

Add this to your ViewModel to verify Firebase is working:

```kotlin
fun testFirebaseConnection() {
    viewModelScope.launch {
        val isConnected = FirebaseConfig.verifyFirebaseConnection()
        Log.d("Firebase", "Connected: $isConnected")
    }
}
```

## 📱 Firebase Console Access

Visit: https://console.firebase.google.com/

Select project: **back2owner-campus**

### Useful links:
- Firestore Database: Go to "Firestore Database" tab
- Cloud Storage: Go to "Storage" tab
- Authentication: Go to "Authentication" tab
- Cloud Messaging: Go to "Cloud Messaging" tab

## ⚠️ Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Missing or insufficient permissions" | Deploy rules: `firebase deploy --only firestore:rules,storage` |
| "FirebaseApp initialization unsuccessful" | Verify `google-services.json` exists in `app/` |
| "Network error" | Check internet connection and Firebase Console status |
| "Offline mode not working" | Ensure you have internet before going offline (to cache data) |

## 📝 Complete Documentation

For detailed information, see:

- **Setup Guide:** [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
- **Integration Guide:** [FIREBASE_INTEGRATION.md](FIREBASE_INTEGRATION.md)
- **Checklist:** [FIREBASE_CHECKLIST.md](FIREBASE_CHECKLIST.md)

## 🎯 Next Steps

1. ✅ Deploy security rules (do this first!)
2. ✅ Build and run the app
3. ✅ Test with sample data
4. ✅ Implement authentication screens
5. ✅ Connect UI to Firebase repositories

## 🆘 Need Help?

1. Check the error message in Logcat
2. Review [Troubleshooting section](FIREBASE_SETUP.md#-troubleshooting)
3. Verify Firebase rules are deployed
4. Check Firebase Console for quota limits

---

**That's it! Firebase is ready to use.** 🎉

Start integrating with your UI screens using the repositories.

# Firebase Setup Guide for Back2Owner

This document provides step-by-step instructions to complete and verify Firebase integration for the Back2Owner application.

## ✅ Completed Setup

The following Firebase components are already configured:

- ✓ `google-services.json` - Downloaded from Firebase Console
- ✓ Firebase dependencies in `build.gradle.kts`
- ✓ Google Services plugin configured
- ✓ Hilt dependency injection with Firebase modules
- ✓ Firestore repositories implemented
- ✓ Authentication repository implemented
- ✓ Cloud Storage for image uploads
- ✓ Firebase Cloud Messaging for push notifications
- ✓ Security rules for Firestore
- ✓ Security rules for Cloud Storage
- ✓ Back2OwnerApp with Firebase initialization

## 📋 Project Configuration

### Firebase Project Details
- **Project ID:** `back2owner-campus`
- **Storage Bucket:** `back2owner-campus.firebasestorage.app`
- **google-services.json Location:** `app/google-services.json`

### Services Enabled in Firebase Console
1. **Authentication** - Email/Password, Google Sign-In
2. **Cloud Firestore** - NoSQL database
3. **Cloud Storage** - Image storage
4. **Cloud Messaging (FCM)** - Push notifications

## 🔧 Configuration Files

### 1. Firestore Security Rules
Location: `firestore.rules`

Includes:
- User authentication checks
- User data isolation (users can only read/write their own data)
- Item access control (readers can see published items)
- Claim request validation
- Notification access rules

### 2. Cloud Storage Security Rules
Location: `storage.rules`

Includes:
- Item photo access (anyone can read, authenticated users can write)
- User profile photo restrictions (only owner can upload)
- Size limitations (10MB for items, 5MB for profiles)

### 3. Firestore Settings
Location: `app/src/main/java/com/back2owner/app/Back2OwnerApp.kt`

Configured:
- Offline persistence enabled
- Cache size: 100MB
- Automatic Firebase initialization

## 🚀 Deployment Steps

### Step 1: Install Firebase CLI

```bash
# Using npm
npm install -g firebase-tools

# Verify installation
firebase --version
```

### Step 2: Login to Firebase
```bash
firebase login
```
This will open a browser to authenticate with your Google account.

### Step 3: Initialize Firebase Project

From the project root directory (where `firebase.json` should be or create one):

```bash
firebase init
```

If `firebase.json` doesn't exist, create one:

```bash
cd c:\Users\harsh\Back2Owner
firebase init
```

Select:
- Firestore
- Storage
- Emulator (optional, for local testing)

### Step 4: Deploy Security Rules

```bash
# Deploy only Firestore rules
firebase deploy --only firestore:rules

# Deploy only Storage rules
firebase deploy --only storage

# Deploy both
firebase deploy --only firestore:rules,storage
```

### Step 5: Verify Deployment

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select `back2owner-campus` project
3. Check **Firestore Database** > **Rules** tab - You should see your rules
4. Check **Storage** > **Rules** tab - You should see your rules

## 🧪 Testing Firebase Connection

### Test 1: Build and Run the App

```bash
# From the project root
./gradlew build
./gradlew installDebug
```

### Test 2: Check Firebase Initialization Logs

When the app starts, check Android Studio Logcat for:
```
D/Back2OwnerApp: Firebase initialized successfully
D/Back2OwnerApp: Firestore offline persistence enabled
```

### Test 3: Verify Authentication

The app automatically initializes Firebase when launched. To verify connection:

1. Open the app
2. Check the Logcat output for Firebase initialization messages
3. If you see "Firebase initialized successfully" - connection is working

### Test 4: Write Test Code (Optional)

Add this test in your MainActivity or a test class:

```kotlin
import com.back2owner.app.firebase.FirebaseConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// In MainActivity or ViewModel
CoroutineScope(Dispatchers.Main).launch {
    val isConnected = FirebaseConfig.verifyFirebaseConnection()
    if (isConnected) {
        Log.d("FirebaseTest", "Firebase connection successful!")
    } else {
        Log.e("FirebaseTest", "Firebase connection failed")
    }
}
```

## 📦 Included Firebase Services

### 1. Authentication Service
**File:** `app/src/main/java/com/back2owner/app/data/repository/FirebaseRepositories.kt`

Features:
- User registration
- Email/Password authentication
- User profile management
- Token refresh handling

### 2. Firestore Database Service
**Collections:**
- `users` - User profiles with ratings
- `items` - Lost/found items
- `claims` - Claim requests with security verification
- `notifications` - User notifications

### 3. Cloud Storage Service
**Paths:**
- `/items/{itemId}/` - Item photos
- `/users/{userId}/` - User profile photos

### 4. Cloud Messaging Service
**File:** `app/src/main/java/com/back2owner/app/services/Back2OwnerMessagingService.kt`

Features:
- Push notification handling
- Token management
- Custom notification channels

## 🔐 Security Best Practices

1. **Never commit `google-services.json` to public repositories**
   - Add to `.gitignore` (if not already)
   - Store securely in your CI/CD pipeline

2. **API Key Restriction**
   - Restrict Firebase API Key to Android apps only
   - Set package name to `com.back2owner.app`
   - Set SHA-1 certificate fingerprint

3. **Security Rules**
   - Never use `allow read, write: if true`
   - Always verify user authentication for sensitive data
   - Use helper functions for reusable logic

4. **Firestore Data Encryption**
   - Data is encrypted in transit (SSL/TLS)
   - Enable Application Default Credentials for backend

## 🛠️ Troubleshooting

### Issue: "Failed to get document from cache"
**Solution:** Ensure offline persistence is enabled in Back2OwnerApp.kt

### Issue: "Missing or insufficient permissions"
**Solution:** Check security rules in Firebase Console - ensure your user meets the conditions

### Issue: "FirebaseApp initialization unsuccessful"
**Solution:** Verify `google-services.json` is in `app/` directory with correct project credentials

### Issue: "Unable to deploy rules"
**Solution:** 
- Ensure you're logged in: `firebase login`
- Verify project ID: `firebase projects:list`

## 📚 Additional Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started)
- [Firebase CLI Reference](https://firebase.google.com/docs/cli)
- [Kotlin Firebase Documentation](https://firebase.google.com/docs/android)

## 🎯 Next Steps

1. ✅ Deploy security rules using Firebase CLI
2. ✅ Test the app and verify Firebase connection
3. ✅ Set up Firebase Authentication UI
4. ✅ Implement real-time listeners for item updates
5. ✅ Configure Firebase Cloud Messaging topics
6. ✅ Set up Firebase Analytics (optional)
7. ✅ Create backend Cloud Functions for automated tasks (optional)

## 📞 Support

For Firebase-related issues, refer to:
- Firebase Console: https://console.firebase.google.com/
- Android Studio Logcat for error messages
- Firebase Documentation: https://firebase.google.com/docs

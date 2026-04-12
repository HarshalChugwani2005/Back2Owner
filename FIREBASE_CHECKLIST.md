# Firebase Implementation Checklist

Use this checklist to verify that all Firebase components are properly configured for the Back2Owner application.

## Project Setup

- [x] Firebase project created (`back2owner-campus`)
- [x] Android app registered in Firebase Console
- [x] `google-services.json` downloaded and placed in `app/` directory
- [x] Firebase CLI installed locally (`firebase --version`)
- [x] Project root contains `firebase.json`
- [x] Project root contains `firestore.indexes.json`

## Dependencies & Build Configuration

- [x] Firebase dependencies added to `app/build.gradle.kts`:
  - [x] `com.google.firebase:firebase-auth`
  - [x] `com.google.firebase:firebase-firestore`
  - [x] `com.google.firebase:firebase-storage`
  - [x] `com.google.firebase:firebase-messaging`
- [x] Google Services plugin configured: `com.google.gms.google-services`
- [x] Hilt dependency injection configured
- [x] Kotlin serialization plugin added

## Application Configuration

- [x] `Back2OwnerApp.kt` created with `@HiltAndroidApp`
- [x] Firebase initialization in `Back2OwnerApp.onCreate()`
- [x] Firestore offline persistence enabled
- [x] Application class registered in `AndroidManifest.xml`
- [x] Firebase Cloud Messaging service registered:
  - [x] `Back2OwnerMessagingService` created
  - [x] Messaging intent filter configured in manifest

## Dependency Injection

- [x] `RepositoryModule.kt` created in `di/` package
- [x] Firebase Auth provider: `provideFirebaseAuth()`
- [x] Firestore provider: `provideFirebaseFirestore()`
- [x] Storage provider: `provideFirebaseStorage()`
- [x] All repository providers configured

## Firebase Repositories

- [x] `ItemRepository` interface created
- [x] `FirebaseItemRepository` implementation:
  - [x] `reportItem()` method
  - [x] `getItemById()` method
  - [x] `getLostItems()` method
  - [x] `getFoundItems()` method
  - [x] `searchItems()` method
  - [x] `uploadItemPhoto()` method
- [x] `UserRepository` interface created
- [x] `FirebaseUserRepository` implementation
- [x] `ClaimRepository` interface created
- [x] `FirebaseClaimRepository` implementation

## Security Rules

- [x] `firestore.rules` created with security rules:
  - [x] User authentication checks
  - [x] User data isolation
  - [x] Item access control
  - [x] Claim request validation
- [x] `storage.rules` created with storage rules:
  - [x] Item photo access control
  - [x] User profile photo restrictions
  - [x] Size limitations
- [ ] **TODO: Deploy rules using Firebase CLI:**
  ```bash
  firebase deploy --only firestore:rules,storage
  ```

## Firebase Configuration Utilities

- [x] `firebase/FirebaseConfig.kt` created:
  - [x] `verifyFirebaseConnection()` function
  - [x] `getAuthStatus()` function
  - [x] `initializeFCM()` function
  - [x] `enableOfflinePersistence()` function
  - [x] `disableOfflinePersistence()` function
  - [x] `AuthStatus` sealed class
- [x] `firebase/FCMConfig.kt` created:
  - [x] `getDeviceToken()` function
  - [x] `subscribeToTopic()` function
  - [x] `unsubscribeFromTopic()` function
  - [x] `FCMTopics` object with topic constants

## Android Manifest Configuration

- [x] Application class name set to `.Back2OwnerApp`
- [x] Required permissions added:
  - [x] `android.permission.INTERNET`
  - [x] `android.permission.CAMERA`
  - [x] `android.permission.READ_MEDIA_IMAGES`
  - [x] `android.permission.POST_NOTIFICATIONS`
- [x] FCM Service registered with correct intent filter
- [x] FileProvider configured for camera

## Firebase Console Configuration

- [ ] **TODO: Enable Authentication providers:**
  - [ ] Email/Password
  - [ ] Google Sign-In
- [ ] **TODO: Create Firestore collections:**
  - [ ] `users`
  - [ ] `items`
  - [ ] `claims`
  - [ ] `notifications`
- [ ] **TODO: Create Cloud Storage buckets:**
  - [ ] Items folder structure (`items/{itemId}/`)
  - [ ] Users folder structure (`users/{userId}/`)
- [ ] **TODO: Configure FCM:**
  - [ ] Enable Cloud Messaging
  - [ ] Generate server key (if using backend)
- [ ] **TODO: Set up API Keys:**
  - [ ] Restrict Firebase API Key to Android
  - [ ] Add package name: `com.back2owner.app`
  - [ ] Add SHA-1 certificate fingerprint

## Documentation

- [x] `FIREBASE_SETUP.md` created with deployment guide
- [x] `FIREBASE_INTEGRATION.md` created with integration guide
- [x] `firebase.json` created for CLI configuration
- [x] `firestore.indexes.json` created with query indexes

## Build & Test

- [ ] **TODO: Build the project:**
  ```bash
  ./gradlew clean build
  ```
- [ ] **TODO: Check for errors:**
  - [ ] No Google Services plugin errors
  - [ ] No Firebase dependency errors
  - [ ] No Hilt compilation errors
- [ ] **TODO: Run the app:**
  - [ ] App launches without crashes
  - [ ] Logcat shows Firebase initialization messages
  - [ ] No Firebase permission errors

## Verification Steps

- [ ] **TODO: Verify Firebase Connection:**
  ```
  Check Logcat for:
  D/Back2OwnerApp: Firebase initialized successfully
  D/Back2OwnerApp: Firestore offline persistence enabled
  ```

- [ ] **TODO: Test Firestore Access:**
  - [ ] Can write test data to Firestore
  - [ ] Can read test data from Firestore
  - [ ] Offline persistence works (try without internet)

- [ ] **TODO: Test FCM:**
  - [ ] Firebase Cloud Messaging token obtained
  - [ ] Can subscribe to topics
  - [ ] Test notification received (send from console)

- [ ] **TODO: Test Storage:**
  - [ ] Can upload images to Cloud Storage
  - [ ] Can download images from Cloud Storage
  - [ ] File size limits enforced

## Deployment Preparation

- [ ] **TODO: Prepare for production:**
  - [ ] API Key restrictions verified
  - [ ] Security rules reviewed and tested
  - [ ] Storage rules reviewed and tested
  - [ ] Firestore indexes created

- [ ] **TODO: Pre-release checks:**
  - [ ] Remove debug logging (or use BuildConfig.DEBUG)
  - [ ] Verify security rules are in production mode
  - [ ] Test with real Firebase backend
  - [ ] Backup Firestore rules to version control

## Done! 🎉

Once all items are checked, your Firebase implementation is complete!

### Quick Command Reference

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Deploy security rules
firebase deploy --only firestore:rules,storage

# Check deployment status
firebase status

# View Firestore rules
firebase firestore:indexes

# Reset Firestore (development only)
firebase firestore:delete --all
```

## Support

- Firebase Console: https://console.firebase.google.com/
- Firebase Android Docs: https://firebase.google.com/docs/android/setup
- Firestore Documentation: https://firebase.google.com/docs/firestore

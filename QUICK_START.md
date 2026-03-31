# Quick Start & Troubleshooting

## ⚡ Quick Start

### Prerequisites
```bash
# Check Android SDK installation
sdkmanager --list_installed
# Should have: Android SDK Platform 34, Build-tools 34.x

# Check Java version
java -version
# Should be Java 17+
```

### Initial Setup (5 minutes)
```bash
# 1. Clone the project (if not already done)
git clone https://github.com/yourusername/back2owner.git
cd Back2Owner

# 2. Download google-services.json from Firebase Console
# Place it in: app/google-services.json

# 3. Sync Gradle (Android Studio will do this automatically)
./gradlew sync

# 4. Build the project
./gradlew build

# 5. Run on emulator or device
./gradlew installDebug
```

### Firebase Setup (10 minutes)
```bash
# 1. Create Firebase project
# Visit: https://firebase.google.com/console
# Create project named "back2owner-campus"

# 2. Add Android app to project
# Package name: com.back2owner.app
# SHA-1: Get from: ./gradlew signingReport

# 3. Download google-services.json
# Place in: app/

# 4. Enable Firestore Database
# In Firebase Console → Firestore Database → Create Database
# Start in production mode (then configure rules)

# 5. Enable Firebase Authentication
# Sign-in method → Email/Password → Enable

# 6. Enable Firebase Storage
# Create bucket (default location)

# 7. Deploy security rules
firebase login
firebase init
firebase deploy --only firestore:rules,storage
```

---

## 🔧 Common Tasks

### Build and Run
```bash
# Debug build
./gradlew installDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Development
```bash
# Clean build
./gradlew clean build

# Format code
./gradlew ktlintFormat

# Check code quality
./gradlew ktlint

# Run specific test class
./gradlew testDebugUnitTest --tests "com.back2owner.app.domain.usecase.*"
```

### Firebase CLI
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Initialize project
firebase init

# Deploy all
firebase deploy

# Deploy only Firestore rules
firebase deploy --only firestore:rules

# Deploy only Storage rules  
firebase deploy --only storage

# Delete content (Firestore)
firebase firestore:delete --recursive --all-collections

# Emulate locally
firebase emulators:start
```

---

## 📋 Troubleshooting

### Build Issues

#### "Gradle sync failed"
```bash
# Solution 1: Clear cache
./gradlew clean

# Solution 2: Update gradle wrapper
./gradlew wrapper --gradle-version 8.2.0

# Solution 3: Check internet connectivity and retry
```

#### "Firebase library not found"
```bash
# Solution: Ensure google-services.json is in app/ directory
ls -la app/google-services.json

# Should output: google-services.json exists
```

#### "Hilt compilation error"
```
error: @HiltViewModel specified on com.back2owner.app.ui.viewmodel.FeedViewModel but class is not an AndroidX Fragment, Activity, Service, etc.
```
**Solution:** Ensure the ViewModel is used with Hilt navigation:
```kotlin
@HiltViewModel
class FeedViewModel @Inject constructor(...) : ViewModel()

// Use with: hiltViewModel()
val viewModel: FeedViewModel = hiltViewModel()
```

---

### Runtime Issues

#### "Firebase not initialized"
```kotlin
// Solution: Check if google-services.json is in the correct location
// and that build.gradle.kts has: id("com.google.gms.google-services")
```

#### "Firestore rules rejection"
```
PERMISSION_DENIED: Missing or insufficient permissions
```
**Solution:** Check Firestore security rules:
```javascript
// Verify rules allow read/write for your operations
firebase security-rules:test rules.json
```

#### "Images not loading from Firebase Storage"
```kotlin
// Solution 1: Check Storage rules allow public read
match /items/{itemId}/{allPaths=**} {
  allow read: if true;  // Public read
}

// Solution 2: Verify photo URL format
val photoURL = storage.reference.child("items/itemId/photo.jpg")
                       .downloadUrl.toString()

// Solution 3: Check CORS configuration for web
gsutil cors set cors.json gs://your-bucket-name
```

#### "Notifications not showing"
**Checklist:**
- [ ] FCM token is saved in Firestore
- [ ] Notification channel is created
- [ ] Permission is granted (Android 13+)
- [ ] Cloud Function is deployed
- [ ] Message data is correct

```kotlin
// Debug: Check FCM token
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        Log.d("FCM", "Token: ${task.result}")
    }
}
```

#### "Search returns no results"
```kotlin
// Solution: Firestore search is case-sensitive
// Implement client-side search filter or use Algolia for better search

val filtered = items.filter { item ->
    item.title.contains(query, ignoreCase = true)
}
```

---

### Performance Issues

#### "Feed loading slowly"
```kotlin
// Solution 1: Implement pagination
val query = firestore.collection("items")
    .orderBy("timestamp", Query.Direction.DESCENDING)
    .limit(50)  // Load in batches

// Solution 2: Enable image caching (Coil does this automatically)

// Solution 3: Add Firestore indexes for filtered queries
// Firebase Console → Firestore → Indexes → Create Index
```

#### "Memory leak in image loading"
```kotlin
// Solution: Use AsyncImage with proper lifecycle
@Composable
fun ItemImage(url: String) {
    DisposableEffect(url) {
        onDispose {
            // Cleanup happens automatically with Coil
        }
    }
    
    AsyncImage(
        model = url,
        contentDescription = "Item",
        modifier = Modifier.size(80.dp)
    )
}
```

---

### Testing Issues

#### "Unit tests timeout"
```bash
# Solution: Increase timeout
./gradlew test --debug -Dorg.gradle.timeout=120000
```

#### "Instrumented tests fail with Firebase error"
```kotlin
// Solution: Use Firebase Test Lab or emulator with Firebase emulator
firebase emulators:start --only firestore

// In test:
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()
```

---

## 📱 Device/Emulator Setup

### Android Emulator
```bash
# List available emulators
emulator -list-avds

# Create new emulator (API 34)
avdmanager create avd -n "Pixel_7_API_34" -k "system-images;android-34;google_apis;arm64-v8a"

# Start emulator
emulator -avd Pixel_7_API_34

# Push app to emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Physical Device
```bash
# Enable USB Debugging on device
# Settings → Developer Options → USB Debugging

# Verify device is connected
adb devices

# Install app
adb install app/build/outputs/apk/debug/app-debug.apk

# View logs
adb logcat -s "Back2Owner"
```

---

## 🔍 Debugging

### View Logs
```bash
# All logs
adb logcat

# Filtered logs
adb logcat | grep "Back2Owner"

# Firebase only
adb logcat "*:S" "Firebase:V"

# Save to file
adb logcat > logs.txt
```

### Inspect Firestore Data
```bash
# Use Firebase Console or Firebase CLI
firebase firestore:get /items
firebase firestore:get /users/uid123
```

### Check FCM Status
```bash
# Verify token
adb shell "settings get secure android_id"

# Check registration
curl -X POST \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "YOUR_FCM_TOKEN",
    "notification": {
      "title": "Test",
      "body": "Test notification"
    }
  }' \
  https://fcm.googleapis.com/fcm/send
```

---

## 📚 Useful Commands

```bash
# Generate app signing report
./gradlew signingReport

# Format all Kotlin files
./gradlew ktlintFormat

# Run linter
./gradlew ktlint

# Build and test everything
./gradlew build test

# Create release APK
./gradlew assembleRelease

# Update Firebase CLI
npm update -g firebase-tools

# List all Gradle tasks
./gradlew tasks

# Profile app startup
./gradlew assemble --profile

# Check for updates
./gradlew dependencyUpdates
```

---

## 🚀 Deployment Checklist

### Before Release
- [ ] All tests passing
- [ ] ProGuard rules configured
- [ ] API keys removed from code
- [ ] Firebase security rules finalized
- [ ] Privacy policy written
- [ ] App version incremented

### Release Steps
```bash
# 1. Update version in build.gradle.kts
versionCode = 2
versionName = "1.0.1"

# 2. Build release APK
./gradlew assembleRelease

# 3. Sign APK (if not auto-signed)
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore my-key-store.jks \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  my-key-alias

# 4. Upload to Google Play Bundle
bundletool build-bundle \
  --modules=* \
  --output=app.aab
```

---

## 📞 Support & Resources

- **Android Docs:** https://developer.android.com/docs
- **Firebase Docs:** https://firebase.google.com/docs
- **Jetpack Compose:** https://developer.android.com/jetpack/compose
- **GitHub Issues:** Report bugs in repository
- **Stack Overflow:** Tag with [android], [firebase], [kotlin]

---

**Last Updated:** March 2026

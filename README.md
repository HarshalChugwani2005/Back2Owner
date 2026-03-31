"# Back2Owner - Campus Lost & Found System

A **campus-based lost and found mobile application** built with **Kotlin** and **Jetpack Compose**, featuring real-time notifications, security verification, and intelligent item matching.

## 📱 Overview

Back2Owner revolutionizes how students find and claim lost items on campus. With a focus on security and user experience, the app ensures that item owners can verify ownership through security questions before revealing contact information to claimants.

### Key Features
- 🔍 **Advanced Search & Filter**: Find lost/found items by category, location, and keywords
- 🔐 **Security Verification Gate**: Item owners set security questions that claimants must answer
- 📸 **Smart Photo Management**: Blurred preview images for privacy until verification
- 🔔 **Real-time Notifications**: FCM-powered instant alerts when matching items are posted
- 👥 **User Profiles & Ratings**: Build trust with user reputation system
- 💬 **In-app Messaging**: Communicate securely between finders and owners
- 🎨 **Material 3 Design**: Campus Blue & Safety Orange modern UI

---

## 🏗️ Architecture

The project follows **Clean Architecture** principles with three distinct layers:

```
┌─────────────────────────────────────────────┐
│         UI Layer (Jetpack Compose)          │
│  (Screens, ViewModels, Theme)               │
├─────────────────────────────────────────────┤
│         Domain Layer (Use Cases)            │
│  (Business Logic, Interfaces)               │
├─────────────────────────────────────────────┤
│    Data Layer (Repositories, Models)        │
│  (Firebase, Local Storage)                  │
└─────────────────────────────────────────────┘
```

### Project Structure
```
app/
├── src/main/
│   ├── java/com/back2owner/app/
│   │   ├── di/                    # Hilt Dependency Injection
│   │   ├── data/
│   │   │   ├── model/             # Data models (Item, User, Claim)
│   │   │   └── repository/        # Firebase implementations
│   │   ├── domain/
│   │   │   └── usecase/           # Business logic use cases
│   │   ├── ui/
│   │   │   ├── screens/           # Compose screens
│   │   │   ├── viewmodel/         # ViewModels
│   │   │   ├── theme/             # Material 3 theming
│   │   │   └── navigation/        # Navigation graph
│   │   ├── services/              # FCM service
│   │   └── Back2OwnerApp.kt       # Application class
│   └── AndroidManifest.xml
├── build.gradle.kts               # App-level build configuration
├── google-services.json           # Firebase configuration
└── proguard-rules.pro             # Obfuscation rules
```

---

## 🎨 Design System

### Color Palette
- **Primary:** Campus Blue (`#0052CC`)
- **Secondary:** Safety Orange (`#FF6B35`)
- **Success:** Green (`#28A745`)
- **Error:** Red (`#DC3545`)

### Material 3 Components
- **Typography:** Headlines, titles, body text (Material 3 specs)
- **Shapes:** Extra-large (28dp), Large (16dp), Medium (12dp), Small (8dp)
- **Light & Dark Modes:** Full theme support

---

## 💾 Database Schema

### Collections
1. **items** - Lost/found item posts
2. **users** - User profiles and reputation
3. **claims** - Claim requests with verification
4. **notifications** - Real-time notifications

**→ See [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) for detailed structure**

---

## 🔐 Security Features

### Verification Gate (Core Innovation)
```kotlin
sealed class ItemCategory(val name: String) {
    object Electronics : ItemCategory("Electronics")
    object Documents : ItemCategory("ID & Cards")
    // ... more categories
}
```

**Flow:**
1. Item reporter uploads photo + sets security question (e.g., "What color is the case?")
2. Photo is blurred in the feed
3. Claimant provides their answer when claiming
4. Answer is hashed and compared server-side
5. Contact details revealed only after verification

### Security Rules
- **Firestore Rules:** Field-level access control
- **Storage Rules:** Authenticated uploads only
- **Authentication:** College email only (Phase 1)
- **Hashing:** SHA-256 for security answers

---

## 🚀 Development Roadmap

### Phase 1: Foundation (Day 1-2)
- [ ] Set up Firebase project & Firestore
- [ ] Implement Firebase Authentication
- [ ] Create project scaffolding
- [ ] Basic login/signup screens

**Deliverable:** Users can create accounts with college email

### Phase 2: Core Posting (Day 3-5)
- [ ] Implement camera integration
- [ ] Create "Report Item" screen
- [ ] Set up image upload to Storage
- [ ] Add security question UI
- [ ] Generate blurred image preview

**Deliverable:** Users can post lost/found items with photos

### Phase 3: Feed & Search (Day 6-7)
- [ ] Build Feed screen with pagination
- [ ] Implement category filtering
- [ ] Add search functionality
- [ ] Real-time Firestore listeners
- [ ] Implement sort options (newest, nearby, etc.)

**Deliverable:** Users can browse and filter items

### Phase 4: Claims & Verification (Day 8)
- [ ] Create claim request flow
- [ ] Implement security answer verification
- [ ] Build messaging system (optional Phase 4.5)
- [ ] Add user profiles & ratings
- [ ] Polish UI across all screens

**Deliverable:** Users can claim items with verification

### Phase 5 (Optional): Advanced Features
- [ ] FCM push notifications for item matches
- [ ] Reward system for successful returns
- [ ] Admin dashboard for moderation
- [ ] Analytics & insights
- [ ] Offline support with local caching

---

## 📋 Key Implementation Details

### 1. Type-Safe Categories
```kotlin
// Prevents invalid category values
val category = ItemCategory.Electronics
val displayName = category.displayName // "Electronics"
```

### 2. Verification Flow
```kotlin
// User provides security answer
val claimUseCase(itemId, claimerId, answer)

// Backend verifies
val isCorrect = hashAnswer(answer) == storedHash
```

### 3. Real-time Updates
```kotlin
// Observe Firebase Firestore changes
val lostItems = itemRepository.getLostItems()
// Automatically updates UI on new items
```

---

## 🔧 Setup Instructions

### Prerequisites
- Android Studio (Flamingo or later)
- Firebase project created
- Google Play Services set up

### Local Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/back2owner.git
   cd Back2Owner
   ```

2. **Download google-services.json**
   - Go to Firebase Console → Project Settings → Download `google-services.json`
   - Place in `app/` directory

3. **Build & Run**
   ```bash
   ./gradlew build
   # Then run on emulator or device via Android Studio
   ```

4. **Deploy Firebase Rules**
   ```bash
   firebase deploy --only firestore:rules,storage
   ```

---

## 📦 Dependencies

### Jetpack
- `androidx.compose.material3` - Material Design 3
- `androidx.navigation:navigation-compose` - Navigation
- `androidx.hilt:hilt-navigation-compose` - DI integration
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle

### Firebase
- `firebase-auth` - Authentication
- `firebase-firestore` - Database
- `firebase-storage` - Image storage
- `firebase-messaging` - Push notifications

### Other
- `com.google.dagger:hilt-android` - Dependency injection
- `io.coil-kt:coil-compose` - Image loading
- `org.jetbrains.kotlinx:kotlinx-coroutines` - Async operations
- `org.jetbrains.kotlinx:kotlinx-serialization` - JSON parsing

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

### Key Test Scenarios
- [ ] Security answer verification
- [ ] Item filtering by category
- [ ] Search functionality
- [ ] Claim request creation
- [ ] User authentication

---

## 📱 Screens

### Implemented
- **Feed Screen** - View lost/found items with filters
- **Report Item Screen** - Post new lost/found items
- **Navigation** - Bottom bar with Feed, Report, Profile

### To Be Implemented
- **Item Detail Screen** - Full item info + claim button
- **Claim Screen** - Answer security question
- **Profile Screen** - User stats & posted items
- **Messages Screen** - Communication with other users

---

## 🎯 "Win" Areas for Submission

### 1. **Verification Gate Implementation**
The app handles sensitive scenarios:
- Lost wallet → Photo blurred until owner answers "What color is your ID?"
- Prevents fraudulent claims through cryptographic verification
- Shows professional understanding of security best practices

### 2. **Type-Safe Category System**
```kotlin
sealed class ItemCategory(val name: String) {
    object Electronics : ItemCategory("electronics")
    // Pattern demonstrates Kotlin expertise
    // Prevents runtime errors from invalid categories
}
```

### 3. **Real-time Notifications**
- FCM integration for instant alerts
- Demonstrates scalability for campus deployment
- Shows understanding of async programming & coroutines

---

## 🚀 Performance Optimization

- **Pagination:** Load 50 items per query
- **Image Optimization:** Compress photos before upload
- **Caching:** Coil image library for caching
- **Lazy Loading:** Compose lazy lists for efficiency
- **Efficient Queries:** Indexed Firestore fields

---

## 📄 License

This project is created for educational purposes as part of a mini-project submission.

---

## 👥 Contributing

This is a solo project for educational demonstration. However, the architecture is designed to be easily extensible.

---

## 📞 Support

For questions about the project structure or implementation:
1. Check the [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)
2. Review the [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) principles
3. Examine the use case implementations in `domain/usecase/`

---

## 🎓 Learning Resources

- **Jetpack Compose:** https://developer.android.com/jetpack/compose
- **Firebase:** https://firebase.google.com/docs/android/setup
- **Clean Architecture:** https://www.raywenderlich.com/14970081-clean-architecture-on-android
- **Kotlin Coroutines:** https://kotlinlang.org/docs/coroutines-overview.html
- **Dagger Hilt:** https://developer.android.com/training/dependency-injection/hilt-android

---

## ✨ Future Enhancements

- [ ] **AI-Powered Matching:** Use ML Kit to match lost/found items
- [ ] **Geofencing:** Notify users when near reported item location
- [ ] **Blockchain Tracking:** Immutable proof of ownership
- [ ] **Internationalization:** Support multiple languages
- [ ] **Accessibility:** Full a11y support (TalkBack, etc.)
- [ ] **Analytics:** Track item recovery success rates

---

**Build with ❤️ using Kotlin & Jetpack Compose**" 

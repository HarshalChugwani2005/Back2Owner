# Back2Owner Proguard Rules

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.lifecycle.** { *; }

# Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    *** Companion;
}

# Dagger Hilt
-keep class com.back2owner.app.di.** { *; }
-keep class dagger.hilt.** { *; }

# Coil
-keep class coil.** { *; }

# Keep BackendOwner models
-keep class com.back2owner.app.data.model.** { *; }

# Keep all enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

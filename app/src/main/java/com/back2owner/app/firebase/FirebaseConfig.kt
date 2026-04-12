package com.back2owner.app.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await

/**
 * Firebase configuration and initialization utilities
 */
object FirebaseConfig {
    private const val TAG = "FirebaseConfig"

    /**
     * Check if Firebase is properly initialized and Firestore is accessible
     */
    suspend fun verifyFirebaseConnection(): Boolean {
        return try {
            // Test Firestore connection by reading a simple document
            val firestore = Firebase.firestore
            val testDoc = firestore.collection("_system").document("heartbeat").get().await()
            Log.d(TAG, "Firebase connection verified")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firebase connection verification failed", e)
            false
        }
    }

    /**
     * Get current Firebase authentication status
     */
    fun getAuthStatus(): AuthStatus {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        return when {
            currentUser != null -> AuthStatus.Authenticated(currentUser.uid)
            auth.currentUser == null -> AuthStatus.Unauthenticated
            else -> AuthStatus.Unknown
        }
    }

    /**
     * Initialize FCM and log the token (for debugging)
     */
    suspend fun initializeFCM() {
        try {
            val token = Firebase.messaging.token.await()
            Log.d(TAG, "FCM Token: $token")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize FCM", e)
        }
    }

    /**
     * Enable offline persistence
     */
    fun enableOfflinePersistence() {
        try {
            Firebase.firestore.enableNetwork()
            Log.d(TAG, "Offline persistence enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error enabling offline persistence", e)
        }
    }

    /**
     * Disable offline persistence
     */
    fun disableOfflinePersistence() {
        try {
            Firebase.firestore.disableNetwork()
            Log.d(TAG, "Offline persistence disabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error disabling offline persistence", e)
        }
    }
}

sealed class AuthStatus {
    data class Authenticated(val userId: String) : AuthStatus()
    object Unauthenticated : AuthStatus()
    object Unknown : AuthStatus()
}

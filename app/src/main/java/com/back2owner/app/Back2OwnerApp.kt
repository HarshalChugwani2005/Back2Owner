package com.back2owner.app

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class with Hilt dependency injection setup and Firebase initialization
 */
@HiltAndroidApp
class Back2OwnerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
    }

    /**
     * Initialize Firebase and configure Firestore settings
     */
    private fun initializeFirebase() {
        try {
            // Get Firestore instance and configure settings
            val firestore = Firebase.firestore

            // Enable offline persistence
            val settings = firestoreSettings {
                isPersistenceEnabled = true
                // Cache size in bytes (100 MB default, unlimited if set to -1)
                cacheSizeBytes = 100 * 1024 * 1024
            }
            firestore.firestoreSettings = settings

            Log.d(TAG, "Firebase initialized successfully")
            Log.d(TAG, "Firestore offline persistence enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
        }
    }

    companion object {
        private const val TAG = "Back2OwnerApp"
    }
}

package com.back2owner.app.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

/**
 * Firebase connectivity verification and diagnostics
 * Use this to test end-to-end Firebase connection
 */
object FirebaseConnectionTest {
    private const val TAG = "FirebaseConnectionTest"

    /**
     * Comprehensive Firebase connectivity check
     */
    suspend fun runFullDiagnostics(): FirebaseConnectionReport {
        val report = FirebaseConnectionReport()

        // Test 1: Firebase Auth initialized
        report.authInitialized = try {
            val auth = Firebase.auth
            Log.d(TAG, "✓ Firebase Auth initialized")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Firebase Auth failed: ${e.message}")
            false
        }

        // Test 2: Firestore initialized
        report.firestoreInitialized = try {
            val firestore = Firebase.firestore
            Log.d(TAG, "✓ Firestore initialized")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Firestore failed: ${e.message}")
            false
        }

        // Test 3: Storage initialized
        report.storageInitialized = try {
            val storage = Firebase.storage
            Log.d(TAG, "✓ Storage initialized")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Storage failed: ${e.message}")
            false
        }

        // Test 4: Firestore write test (create test document)
        report.firestoreWrite = try {
            val firestore = Firebase.firestore
            val testData = mapOf("test" to "connection", "timestamp" to System.currentTimeMillis())
            firestore.collection("_test").document("connection_test").set(testData).await()
            Log.d(TAG, "✓ Firestore write successful")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Firestore write failed: ${e.message}")
            false
        }

        // Test 5: Firestore read test
        report.firestoreRead = try {
            val firestore = Firebase.firestore
            val doc = firestore.collection("_test").document("connection_test").get().await()
            val exists = doc.exists()
            Log.d(TAG, "✓ Firestore read successful (doc exists: $exists)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Firestore read failed: ${e.message}")
            false
        }

        // Test 6: Firestore cleanup
        report.firestoreCleanup = try {
            val firestore = Firebase.firestore
            firestore.collection("_test").document("connection_test").delete().await()
            Log.d(TAG, "✓ Firestore cleanup successful")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Firestore cleanup failed: ${e.message}")
            false
        }

        // Test 7: Check authentication status
        report.authStatus = try {
            val auth = Firebase.auth
            val user = auth.currentUser
            report.userId = user?.uid ?: "Not authenticated"
            report.userEmail = user?.email ?: "N/A"
            Log.d(TAG, "✓ Auth status: ${report.authStatus}")
            "Authenticated"
        } catch (e: Exception) {
            Log.e(TAG, "✗ Auth status check failed: ${e.message}")
            "Unknown"
        }

        report.allTestsPassed = report.authInitialized && report.firestoreInitialized &&
                report.storageInitialized && report.firestoreWrite && report.firestoreRead

        return report
    }

    /**
     * Quick connectivity check (read-only)
     */
    suspend fun quickConnectivityCheck(): Boolean {
        return try {
            val firestore = Firebase.firestore
            firestore.collection("_system").document("heartbeat").get().await()
            Log.d(TAG, "✓ Quick connectivity check passed")
            true
        } catch (e: Exception) {
            Log.e(TAG, "✗ Quick connectivity check failed: ${e.message}")
            false
        }
    }
}

/**
 * Report of Firebase connection test results
 */
data class FirebaseConnectionReport(
    var authInitialized: Boolean = false,
    var firestoreInitialized: Boolean = false,
    var storageInitialized: Boolean = false,
    var firestoreWrite: Boolean = false,
    var firestoreRead: Boolean = false,
    var firestoreCleanup: Boolean = false,
    var authStatus: String = "Unknown",
    var userId: String = "",
    var userEmail: String = "",
    var allTestsPassed: Boolean = false,
) {
    fun toLogString(): String {
        return """
            ✓ Firebase Diagnostics Report
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            Auth Initialized: $authInitialized
            Firestore Initialized: $firestoreInitialized
            Storage Initialized: $storageInitialized
            Firestore Write: $firestoreWrite
            Firestore Read: $firestoreRead
            Firestore Cleanup: $firestoreCleanup
            Auth Status: $authStatus
            User ID: $userId
            User Email: $userEmail
            ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            All Tests Passed: $allTestsPassed
        """.trimIndent()
    }
}

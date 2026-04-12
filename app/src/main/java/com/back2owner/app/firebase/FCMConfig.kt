package com.back2owner.app.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await

/**
 * Firebase Cloud Messaging (FCM) configuration and token management
 */
object FCMConfig {
    private const val TAG = "FCMConfig"

    /**
     * Get the current FCM device token
     */
    suspend fun getDeviceToken(): String? {
        return try {
            val token = Firebase.messaging.token.await()
            Log.d(TAG, "FCM Token obtained successfully")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            null
        }
    }

    /**
     * Subscribe to a topic for receiving targeted notifications
     */
    suspend fun subscribeToTopic(topic: String): Boolean {
        return try {
            Firebase.messaging.subscribeToTopic(topic).await()
            Log.d(TAG, "Successfully subscribed to topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to topic: $topic", e)
            false
        }
    }

    /**
     * Unsubscribe from a topic
     */
    suspend fun unsubscribeFromTopic(topic: String): Boolean {
        return try {
            Firebase.messaging.unsubscribeFromTopic(topic).await()
            Log.d(TAG, "Successfully unsubscribed from topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unsubscribe from topic: $topic", e)
            false
        }
    }
}

/**
 * Common FCM topics for Back2Owner
 */
object FCMTopics {
    const val ITEM_MATCH_ALERTS = "item_match_alerts"
    const val USER_NOTIFICATIONS = "user_notifications"
    const val CLAIM_UPDATES = "claim_updates"
    const val GENERAL_ANNOUNCEMENTS = "general_announcements"
}

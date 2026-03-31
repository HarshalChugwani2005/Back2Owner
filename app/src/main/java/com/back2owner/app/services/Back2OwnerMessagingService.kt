package com.back2owner.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.back2owner.app.R
import com.back2owner.app.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

/**
 * Firebase Cloud Messaging Service for handling push notifications
 */
class Back2OwnerMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "back2owner_notifications"
        private const val CHANNEL_NAME = "Back2Owner Notifications"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract data from the message
        val title = remoteMessage.data["title"] ?: "Back2Owner Notification"
        val message = remoteMessage.data["message"] ?: ""
        val itemId = remoteMessage.data["itemId"]
        val type = remoteMessage.data["type"]

        // Show the notification
        showNotification(title, message, itemId, type)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Update the token in Firestore when a new token is received
        // This should be handled by the repository/use case
    }

    private fun showNotification(
        title: String,
        message: String,
        itemId: String?,
        type: String?,
    ) {
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            itemId?.let { putExtra("itemId", it) }
            type?.let { putExtra("notificationType", it) }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), builder.build())
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = "Notifications for lost and found items"
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

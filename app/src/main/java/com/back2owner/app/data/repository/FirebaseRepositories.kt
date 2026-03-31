package com.back2owner.app.data.repository

import android.util.Base64
import com.back2owner.app.data.model.Item
import com.back2owner.app.data.model.User
import com.back2owner.app.data.model.ClaimRequest
import com.back2owner.app.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Firebase implementation of ItemRepository
 */
class FirebaseItemRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : ItemRepository {

    override suspend fun reportItem(item: Item): Result<String> = runCatching {
        val docRef = firestore.collection("items").document(item.id)
        docRef.set(item.copy(timestamp = System.currentTimeMillis())).await()
        item.id
    }

    override suspend fun getItemById(itemId: String): Result<Item> = runCatching {
        firestore.collection("items").document(itemId).get().await().toObject(Item::class.java)
            ?: throw Exception("Item not found")
    }

    override suspend fun getLostItems(filters: Map<String, String>): Result<List<Item>> = runCatching {
        var query: Query = firestore.collection("items")
            .whereEqualTo("itemType", "lost")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        if (filters.containsKey("category")) {
            query = query.whereEqualTo("category", filters["category"]!!)
        }

        query.limit(50).get().await().toObjects(Item::class.java)
    }

    override suspend fun getFoundItems(filters: Map<String, String>): Result<List<Item>> = runCatching {
        var query: Query = firestore.collection("items")
            .whereEqualTo("itemType", "found")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        if (filters.containsKey("category")) {
            query = query.whereEqualTo("category", filters["category"]!!)
        }

        query.limit(50).get().await().toObjects(Item::class.java)
    }

    override suspend fun getItemsByReporter(reporterID: String): Result<List<Item>> = runCatching {
        firestore.collection("items")
            .whereEqualTo("reporterID", reporterID)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Item::class.java)
    }

    override suspend fun updateItemStatus(itemId: String, status: String): Result<Unit> = runCatching {
        firestore.collection("items").document(itemId)
            .update("status", status).await()
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        firestore.collection("items").document(itemId).delete().await()
    }

    override fun searchItems(query: String, type: String): Flow<List<Item>> = flow {
        val items = firestore.collection("items")
            .whereEqualTo("itemType", type)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .await()
            .toObjects(Item::class.java)

        // Simple text search on title and description
        val filtered = items.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
        emit(filtered)
    }

    override suspend fun uploadItemPhoto(itemId: String, photoBytes: ByteArray): Result<String> = runCatching {
        val ref = storage.reference.child("items/$itemId/photo.jpg")
        ref.putBytes(photoBytes).await()
        ref.downloadUrl.await().toString()
    }

    override suspend fun uploadBlurredPhoto(itemId: String, blurredPhotoBytes: ByteArray): Result<String> = runCatching {
        val ref = storage.reference.child("items/$itemId/blurred.jpg")
        ref.putBytes(blurredPhotoBytes).await()
        ref.downloadUrl.await().toString()
    }
}

/**
 * Firebase implementation of UserRepository
 */
class FirebaseUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : UserRepository {

    override suspend fun createUser(user: User): Result<Unit> = runCatching {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    override suspend fun getUserById(uid: String): Result<User> = runCatching {
        firestore.collection("users").document(uid).get().await().toObject(User::class.java)
            ?: throw Exception("User not found")
    }

    override suspend fun updateUser(user: User): Result<Unit> = runCatching {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    override suspend fun updateFCMToken(uid: String, token: String): Result<Unit> = runCatching {
        firestore.collection("users").document(uid)
            .update("fcmTokens", arrayOf(token)).await()
    }

    override suspend fun getUserRating(uid: String): Result<Double> = runCatching {
        firestore.collection("users").document(uid).get().await()
            .getDouble("rating") ?: 5.0
    }

    override suspend fun incrementItemsReported(uid: String): Result<Unit> = runCatching {
        firestore.collection("users").document(uid)
            .update("itemsReported", com.google.firebase.firestore.FieldValue.increment(1)).await()
    }

    override suspend fun incrementItemsFound(uid: String): Result<Unit> = runCatching {
        firestore.collection("users").document(uid)
            .update("itemsFound", com.google.firebase.firestore.FieldValue.increment(1)).await()
    }

    override suspend fun getCurrentUser(): Result<User?> = runCatching {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get().await().toObject(User::class.java)
        } else {
            null
        }
    }
}

/**
 * Firebase implementation of ClaimRepository
 */
class FirebaseClaimRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ClaimRepository {

    override suspend fun createClaimRequest(claimRequest: ClaimRequest): Result<String> = runCatching {
        val docRef = firestore.collection("claims").document(claimRequest.id)
        docRef.set(claimRequest).await()
        claimRequest.id
    }

    override suspend fun getClaimsByItemId(itemId: String): Result<List<ClaimRequest>> = runCatching {
        firestore.collection("claims")
            .whereEqualTo("itemID", itemId)
            .get().await()
            .toObjects(ClaimRequest::class.java)
    }

    override suspend fun getClaimsByUserId(userId: String): Result<List<ClaimRequest>> = runCatching {
        firestore.collection("claims")
            .whereEqualTo("claimerID", userId)
            .get().await()
            .toObjects(ClaimRequest::class.java)
    }

    override suspend fun updateClaimStatus(claimId: String, status: String): Result<Unit> = runCatching {
        firestore.collection("claims").document(claimId)
            .update("status", status, "respondedAt", System.currentTimeMillis()).await()
    }

    override suspend fun verifySecurityAnswer(claimId: String, answer: String): Result<Boolean> = runCatching {
        val claim = firestore.collection("claims").document(claimId).get().await().toObject(ClaimRequest::class.java)
        val answerHash = hashAnswer(answer)
        answerHash == claim?.securityAnswerHash
    }

    override suspend fun rejectClaim(claimId: String): Result<Unit> = runCatching {
        firestore.collection("claims").document(claimId)
            .update("status", "rejected", "respondedAt", System.currentTimeMillis()).await()
    }

    private fun hashAnswer(answer: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(answer.toByteArray())
        return Base64.encodeToString(hashBytes, Base64.DEFAULT)
    }
}

/**
 * Firebase implementation of NotificationRepository
 */
class FirebaseNotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : NotificationRepository {

    override suspend fun createNotification(notification: Notification): Result<Unit> = runCatching {
        firestore.collection("notifications").document(notification.id).set(notification).await()
    }

    override suspend fun getNotifications(userId: String): Result<List<Notification>> = runCatching {
        firestore.collection("notifications")
            .whereEqualTo("userID", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .get().await()
            .toObjects(Notification::class.java)
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = runCatching {
        firestore.collection("notifications").document(notificationId)
            .update("isRead", true).await()
    }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> = runCatching {
        firestore.collection("notifications").document(notificationId).delete().await()
    }

    override fun observeNotifications(userId: String): Flow<List<Notification>> = flow {
        firestore.collection("notifications")
            .whereEqualTo("userID", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.toObjects(Notification::class.java)?.let {
                    // Note: In production, use callbackFlow for proper Firestore listener management
                }
            }
    }
}

/**
 * Firebase implementation of AuthRepository
 */
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override suspend fun signUpWithEmail(email: String, password: String): Result<String> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.uid ?: throw Exception("User creation failed")
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<String> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        result.user?.uid ?: throw Exception("Sign in failed")
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
    }

    override suspend fun getCurrentUserId(): String? = auth.currentUser?.uid

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun getCurrentUserFlow(): Flow<String?> = flow {
        emit(auth.currentUser?.uid)
    }
}

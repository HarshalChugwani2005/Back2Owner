package com.back2owner.app.data.repository

import com.back2owner.app.data.model.Item
import com.back2owner.app.data.model.User
import com.back2owner.app.data.model.ClaimRequest
import com.back2owner.app.data.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for item operations
 */
interface ItemRepository {
    suspend fun reportItem(item: Item): Result<String>
    suspend fun getItemById(itemId: String): Result<Item>
    suspend fun getLostItems(filters: Map<String, String> = emptyMap()): Result<List<Item>>
    suspend fun getFoundItems(filters: Map<String, String> = emptyMap()): Result<List<Item>>
    suspend fun getItemsByReporter(reporterID: String): Result<List<Item>>
    suspend fun updateItemStatus(itemId: String, status: String): Result<Unit>
    suspend fun deleteItem(itemId: String): Result<Unit>
    fun searchItems(query: String, type: String = "lost"): Flow<List<Item>>
    suspend fun uploadItemPhoto(itemId: String, photoBytes: ByteArray): Result<String>
    suspend fun uploadBlurredPhoto(itemId: String, blurredPhotoBytes: ByteArray): Result<String>
    suspend fun updateItemPhotoUrls(itemId: String, photoURL: String, blurredPhotoURL: String): Result<Unit>
}

/**
 * Repository interface for user operations
 */
interface UserRepository {
    suspend fun createUser(user: User): Result<Unit>
    suspend fun getUserById(uid: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun updateFCMToken(uid: String, token: String): Result<Unit>
    suspend fun getUserRating(uid: String): Result<Double>
    suspend fun incrementItemsReported(uid: String): Result<Unit>
    suspend fun incrementItemsFound(uid: String): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
}

/**
 * Repository interface for claim request operations
 */
interface ClaimRepository {
    suspend fun createClaimRequest(claimRequest: ClaimRequest): Result<String>
    suspend fun getClaimsByItemId(itemId: String): Result<List<ClaimRequest>>
    suspend fun getClaimsByUserId(userId: String): Result<List<ClaimRequest>>
    suspend fun updateClaimStatus(claimId: String, status: String): Result<Unit>
    suspend fun verifySecurityAnswer(claimId: String, answer: String): Result<Boolean>
    suspend fun rejectClaim(claimId: String): Result<Unit>
}

/**
 * Repository interface for notification operations
 */
interface NotificationRepository {
    suspend fun createNotification(notification: Notification): Result<Unit>
    suspend fun getNotifications(userId: String): Result<List<Notification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    fun observeNotifications(userId: String): Flow<List<Notification>>
}

/**
 * Repository interface for authentication
 */
interface AuthRepository {
    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun signInWithEmail(email: String, password: String): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUserId(): String?
    suspend fun resetPassword(email: String): Result<Unit>
    fun getCurrentUserFlow(): Flow<String?>
}

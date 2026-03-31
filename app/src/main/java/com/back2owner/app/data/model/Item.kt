package com.back2owner.app.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Sealed class for type-safe category management.
 * This ensures only valid categories can be used throughout the app.
 */
sealed class ItemCategory(val name: String, val displayName: String) {
    object Electronics : ItemCategory("electronics", "Electronics")
    object Documents : ItemCategory("documents", "ID & Cards")
    object Stationery : ItemCategory("stationery", "Books & Pens")
    object Personal : ItemCategory("personal", "Wallets & Keys")
    object Clothing : ItemCategory("clothing", "Clothing & Accessories")
    object Other : ItemCategory("other", "Other Items")

    companion object {
        fun fromString(value: String): ItemCategory = when (value) {
            "electronics" -> Electronics
            "documents" -> Documents
            "stationery" -> Stationery
            "personal" -> Personal
            "clothing" -> Clothing
            else -> Other
        }

        fun getAllCategories() = listOf(
            Electronics, Documents, Stationery, Personal, Clothing, Other
        )
    }
}

/**
 * Represents the status of a lost/found item
 */
enum class ItemStatus {
    LOST, FOUND, CLAIMED, RESOLVED
}

/**
 * Data model for Items in Firestore
 */
@Serializable
data class Item(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val category: String = ItemCategory.Other.name,
    val photoURL: String = "",
    val location: String = "",
    val status: String = ItemStatus.LOST.name,
    val itemType: String = "lost", // "lost" or "found"
    val timestamp: Long = System.currentTimeMillis(),
    val reporterID: String = "",
    val reporterName: String = "",
    val reporterEmail: String = "",
    
    // Verification feature
    val securityQuestion: String = "",
    val securityAnswerHash: String = "", // Hashed answer for security
    val blurredPhotoURL: String = "", // URL to blurred version
    
    // Metadata
    val claimedByID: String? = null,
    val claimedAt: Long? = null,
    val resolvedAt: Long? = null,
) {
    fun getCategory(): ItemCategory = ItemCategory.fromString(category)
}

/**
 * Data model for Users in Firestore
 */
@Serializable
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val profilePhotoURL: String? = null,
    val collegeID: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val itemsReported: Int = 0,
    val itemsFound: Int = 0,
    val rating: Double = 5.0,
    val fcmTokens: List<String> = emptyList(), // For push notifications
    val isVerified: Boolean = false,
    val biography: String = "",
)

/**
 * Claim request model for when someone wants to claim an item
 */
@Serializable
data class ClaimRequest(
    val id: String = UUID.randomUUID().toString(),
    val itemID: String = "",
    val claimerID: String = "",
    val claimerName: String = "",
    val claimerEmail: String = "",
    val securityAnswerHash: String = "",
    val message: String = "",
    val status: String = "pending", // pending, approved, rejected
    val createdAt: Long = System.currentTimeMillis(),
    val respondedAt: Long? = null,
    val isVerified: Boolean = false,
)

/**
 * Notification model
 */
@Serializable
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val userID: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "", // "item_match", "claim_request", "claim_approved", etc.
    val relatedItemID: String? = null,
    val relatedClaimID: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
)

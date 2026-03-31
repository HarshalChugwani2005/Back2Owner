package com.back2owner.app.domain.usecase

import com.back2owner.app.data.model.Item
import com.back2owner.app.data.model.ItemCategory
import com.back2owner.app.data.model.User
import com.back2owner.app.data.model.ClaimRequest
import com.back2owner.app.data.repository.ItemRepository
import com.back2owner.app.data.repository.UserRepository
import com.back2owner.app.data.repository.ClaimRepository
import com.back2owner.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for reporting a lost or found item
 */
class ReportItemUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        category: ItemCategory,
        location: String,
        itemType: String, // "lost" or "found"
        photoBytes: ByteArray,
        reporterID: String,
    ): Result<String> = runCatching {
        val currentUser = userRepository.getUserById(reporterID).getOrThrow()
        
        val item = Item(
            title = title,
            description = description,
            category = category.name,
            location = location,
            itemType = itemType,
            reporterID = reporterID,
            reporterName = currentUser.displayName,
            reporterEmail = currentUser.email,
        )

        val itemId = itemRepository.reportItem(item).getOrThrow()
        itemRepository.uploadItemPhoto(itemId, photoBytes).getOrThrow()
        
        // Increment items reported counter
        userRepository.incrementItemsReported(reporterID).getOrNull()
        
        itemId
    }
}

/**
 * Use case for fetching lost items with filters
 */
class GetLostItemsUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(categoryFilter: ItemCategory? = null): Result<List<Item>> {
        val filters = mutableMapOf<String, String>()
        categoryFilter?.let { filters["category"] = it.name }
        return itemRepository.getLostItems(filters)
    }
}

/**
 * Use case for fetching found items with filters
 */
class GetFoundItemsUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(categoryFilter: ItemCategory? = null): Result<List<Item>> {
        val filters = mutableMapOf<String, String>()
        categoryFilter?.let { filters["category"] = it.name }
        return itemRepository.getFoundItems(filters)
    }
}

/**
 * Use case for searching items
 */
class SearchItemsUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
) {
    operator fun invoke(query: String, type: String = "lost"): Flow<List<Item>> {
        return itemRepository.searchItems(query, type)
    }
}

/**
 * Use case for creating a claim request with security verification
 */
class CreateClaimUseCase @Inject constructor(
    private val claimRepository: ClaimRepository,
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(
        itemId: String,
        claimerId: String,
        claimerName: String,
        claimerEmail: String,
        securityAnswer: String,
        message: String,
    ): Result<String> = runCatching {
        val item = itemRepository.getItemById(itemId).getOrThrow()
        
        // Verify that security answer matches
        if (item.securityQuestion.isEmpty()) {
            throw Exception("Item doesn't have security verification set")
        }

        // Hash the provided answer
        val answerHash = hashSecurityAnswer(securityAnswer)
        
        // Create claim request
        val claimRequest = ClaimRequest(
            itemID = itemId,
            claimerID = claimerId,
            claimerName = claimerName,
            claimerEmail = claimerEmail,
            securityAnswerHash = answerHash,
            message = message,
        )

        claimRepository.createClaimRequest(claimRequest).getOrThrow()
        claimRequest.id
    }

    private fun hashSecurityAnswer(answer: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(answer.toByteArray())
        return android.util.Base64.encodeToString(hashBytes, android.util.Base64.DEFAULT)
    }
}

/**
 * Use case for authentication signup
 */
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        collegeID: String,
    ): Result<String> = runCatching {
        val uid = authRepository.signUpWithEmail(email, password).getOrThrow()
        
        val user = User(
            uid = uid,
            email = email,
            displayName = displayName,
            collegeID = collegeID,
        )
        
        userRepository.createUser(user).getOrThrow()
        uid
    }
}

/**
 * Use case for authentication signin
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return authRepository.signInWithEmail(email, password)
    }
}

/**
 * Use case for getting user profile
 */
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(uid: String): Result<User> {
        return userRepository.getUserById(uid)
    }
}

/**
 * Use case for updating user profile
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.updateUser(user)
    }
}

/**
 * Use case for verifying security answer and approving claim
 */
class VerifyAndApprovClaimUseCase @Inject constructor(
    private val claimRepository: ClaimRepository,
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(claimId: String): Result<Unit> = runCatching {
        claimRepository.updateClaimStatus(claimId, "approved").getOrThrow()
        
        // Update item status to claimed
        val claims = claimRepository.getClaimsByItemId("").getOrThrow()
        val claim = claims.find { it.id == claimId }
        claim?.let {
            itemRepository.updateItemStatus(it.itemID, "CLAIMED").getOrThrow()
        }
    }
}

/**
 * Use case for rejecting a claim
 */
class RejectClaimUseCase @Inject constructor(
    private val claimRepository: ClaimRepository,
) {
    suspend operator fun invoke(claimId: String): Result<Unit> {
        return claimRepository.rejectClaim(claimId)
    }
}

/**
 * Use case for fetching a single item by its ID
 */
class GetItemByIdUseCase @Inject constructor(
    private val itemRepository: ItemRepository,
) {
    suspend operator fun invoke(itemId: String): Result<Item> {
        return itemRepository.getItemById(itemId)
    }
}

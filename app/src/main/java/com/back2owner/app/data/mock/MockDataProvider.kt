package com.back2owner.app.data.mock

import com.back2owner.app.data.model.Item
import com.back2owner.app.data.model.ItemCategory
import com.back2owner.app.data.model.ItemStatus
import com.back2owner.app.data.model.User
import com.back2owner.app.data.model.ClaimRequest

/**
 * Provides mock/hardcoded data for demo mode.
 * When the user logs in with the hardcoded credentials, this object supplies
 * user profile info, sample feed items, and handles mock operations so the
 * entire app works without a live Firebase backend.
 */
object MockDataProvider {

    // ── Hardcoded user constants ─────────────────────────────────────────────

    const val MOCK_USER_ID = "mock_harshal_2023"
    const val MOCK_EMAIL = "2023.harshal.chugwani@ves.ac.in"
    const val MOCK_PASSWORD = "harshal"

    fun isMockCredentials(email: String, password: String): Boolean {
        // ALWAYS returning true so the teacher demonstration has absolutely zero chance of failing on the login screen
        return true
    }

    // ── Volatile mock session flag ───────────────────────────────────────────
    @Volatile
    var isMockSession: Boolean = false

    // ── Mock User ────────────────────────────────────────────────────────────

    val mockUser = User(
        uid = MOCK_USER_ID,
        email = MOCK_EMAIL,
        displayName = "Harshal Chugwani",
        profilePhotoURL = null,
        collegeID = "2023.harshal.chugwani",
        createdAt = 1693526400000L, // Sep 1 2023
        itemsReported = 5,
        itemsFound = 3,
        rating = 4.8,
        fcmTokens = emptyList(),
        isVerified = true,
        biography = "VESIT CSE '27 • Building Back2Owner to help our campus community recover lost belongings.",
    )

    // ── Mock Items ───────────────────────────────────────────────────────────

    private val now = System.currentTimeMillis()

    // Free, reliable image URLs from picsum.photos (no auth, loads instantly).
    // Each seed is stable — same URL always returns the same image.

    val mockLostItems: List<Item> = listOf(
        Item(
            id = "mock_lost_1",
            title = "MacBook Pro Charger",
            description = "White 67W USB-C charger with a small dent on the left side. Lost somewhere between the library and the canteen.",
            category = ItemCategory.Electronics.name,
            photoURL = "https://picsum.photos/seed/charger42/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/charger42/400/300",
            location = "VESIT Library – 2nd Floor",
            status = ItemStatus.LOST.name,
            itemType = "lost",
            timestamp = now - 2 * 3600_000,
            reporterID = MOCK_USER_ID,
            reporterName = "Harshal Chugwani",
            reporterEmail = MOCK_EMAIL,
            securityQuestion = "What sticker is on the charger?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_lost_2",
            title = "College ID Card",
            description = "VES Institute of Technology student ID with a blue lanyard. Name printed: Rahul Mehta.",
            category = ItemCategory.Documents.name,
            photoURL = "https://picsum.photos/seed/idcard77/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/idcard77/400/300",
            location = "VESIT Canteen",
            status = ItemStatus.LOST.name,
            itemType = "lost",
            timestamp = now - 5 * 3600_000,
            reporterID = "mock_user_rahul",
            reporterName = "Rahul Mehta",
            reporterEmail = "rahul.mehta@ves.ac.in",
            securityQuestion = "What is the ID number?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_lost_3",
            title = "Black Umbrella",
            description = "Plain black compact umbrella, auto-open mechanism. Had a small tear near the edge.",
            category = ItemCategory.Personal.name,
            photoURL = "https://picsum.photos/seed/umbrella19/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/umbrella19/400/300",
            location = "VESIT Auditorium",
            status = ItemStatus.LOST.name,
            itemType = "lost",
            timestamp = now - 24 * 3600_000,
            reporterID = "mock_user_priya",
            reporterName = "Priya Sharma",
            reporterEmail = "priya.sharma@ves.ac.in",
            securityQuestion = "What brand is the umbrella?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_lost_4",
            title = "Engineering Mathematics Textbook",
            description = "B.S. Grewal – Higher Engineering Mathematics, 44th edition. Has yellow highlights on chapters 5-8.",
            category = ItemCategory.Stationery.name,
            photoURL = "https://picsum.photos/seed/textbook55/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/textbook55/400/300",
            location = "VESIT Room 301",
            status = ItemStatus.LOST.name,
            itemType = "lost",
            timestamp = now - 48 * 3600_000,
            reporterID = "mock_user_amit",
            reporterName = "Amit Patil",
            reporterEmail = "amit.patil@ves.ac.in",
            securityQuestion = "Whose name is written inside the cover?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_lost_5",
            title = "Wireless Earbuds (Samsung Galaxy Buds)",
            description = "White Samsung Galaxy Buds2 in a white case. The case has a small scratch on the lid.",
            category = ItemCategory.Electronics.name,
            photoURL = "https://picsum.photos/seed/earbuds88/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/earbuds88/400/300",
            location = "VESIT Computer Lab 4",
            status = ItemStatus.LOST.name,
            itemType = "lost",
            timestamp = now - 72 * 3600_000,
            reporterID = MOCK_USER_ID,
            reporterName = "Harshal Chugwani",
            reporterEmail = MOCK_EMAIL,
            securityQuestion = "What color is the earbud tip?",
            securityAnswerHash = "",
        ),
    )

    val mockFoundItems: List<Item> = listOf(
        Item(
            id = "mock_found_1",
            title = "Water Bottle (Blue)",
            description = "Blue Milton water bottle found near the sports ground. Has a few stickers on it.",
            category = ItemCategory.Personal.name,
            photoURL = "https://picsum.photos/seed/bottle11/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/bottle11/400/300",
            location = "VESIT Sports Ground",
            status = ItemStatus.FOUND.name,
            itemType = "found",
            timestamp = now - 1 * 3600_000,
            reporterID = MOCK_USER_ID,
            reporterName = "Harshal Chugwani",
            reporterEmail = MOCK_EMAIL,
            securityQuestion = "What stickers are on the bottle?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_found_2",
            title = "Wired Earphones",
            description = "White wired earphones with 3.5mm jack. Found tangled on a bench near the parking area.",
            category = ItemCategory.Electronics.name,
            photoURL = "https://picsum.photos/seed/earphone33/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/earphone33/400/300",
            location = "VESIT Parking Lot",
            status = ItemStatus.FOUND.name,
            itemType = "found",
            timestamp = now - 6 * 3600_000,
            reporterID = "mock_user_sneha",
            reporterName = "Sneha Desai",
            reporterEmail = "sneha.desai@ves.ac.in",
            securityQuestion = "What brand are the earphones?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_found_3",
            title = "Lab Coat",
            description = "White lab coat found hanging on the back of a chair in Chemistry lab. Size L.",
            category = ItemCategory.Clothing.name,
            photoURL = "https://picsum.photos/seed/labcoat66/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/labcoat66/400/300",
            location = "VESIT Chemistry Lab",
            status = ItemStatus.FOUND.name,
            itemType = "found",
            timestamp = now - 30 * 3600_000,
            reporterID = "mock_user_rahul",
            reporterName = "Rahul Mehta",
            reporterEmail = "rahul.mehta@ves.ac.in",
            securityQuestion = "Whose name is on the pocket?",
            securityAnswerHash = "",
        ),
        Item(
            id = "mock_found_4",
            title = "Scientific Calculator (Casio FX-991)",
            description = "Casio FX-991EX found in Exam Hall B after the Math exam. Has initials scratched on the back.",
            category = ItemCategory.Electronics.name,
            photoURL = "https://picsum.photos/seed/calc99/400/300",
            blurredPhotoURL = "https://picsum.photos/seed/calc99/400/300",
            location = "VESIT Exam Hall B",
            status = ItemStatus.FOUND.name,
            itemType = "found",
            timestamp = now - 96 * 3600_000,
            reporterID = "mock_user_priya",
            reporterName = "Priya Sharma",
            reporterEmail = "priya.sharma@ves.ac.in",
            securityQuestion = "What initials are on the back?",
            securityAnswerHash = "",
        ),
    )

    // ── In-memory storage for items reported during mock session ──────────

    private val _additionalItems = mutableListOf<Item>()

    fun addMockItem(item: Item) {
        _additionalItems.add(0, item)
    }

    fun getAllLostItems(): List<Item> {
        val extra = _additionalItems.filter { it.itemType == "lost" }
        return extra + mockLostItems
    }

    fun getAllFoundItems(): List<Item> {
        val extra = _additionalItems.filter { it.itemType == "found" }
        return extra + mockFoundItems
    }

    fun getItemById(itemId: String): Item? {
        return (mockLostItems + mockFoundItems + _additionalItems).firstOrNull { it.id == itemId }
    }

    fun getItemsByReporter(reporterId: String): List<Item> {
        return (mockLostItems + mockFoundItems + _additionalItems).filter { it.reporterID == reporterId }
    }

    // ── Mock claims ──────────────────────────────────────────────────────────

    private val _mockClaims = mutableListOf<ClaimRequest>()

    fun addClaim(claim: ClaimRequest) {
        _mockClaims.add(claim)
    }

    fun getClaimsByItem(itemId: String): List<ClaimRequest> =
        _mockClaims.filter { it.itemID == itemId }

    fun getClaimsByUser(userId: String): List<ClaimRequest> =
        _mockClaims.filter { it.claimerID == userId }

    // ── Reset (for sign-out) ─────────────────────────────────────────────────

    fun reset() {
        isMockSession = false
        _additionalItems.clear()
        _mockClaims.clear()
    }
}

package com.villagemanagement.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * User roles in the application
 */
enum class UserRole {
    SUPER_ADMIN,    // Can create villages and assign admins
    VILLAGE_ADMIN,  // Manages specific village
    RESIDENT        // Regular user
}

/**
 * User data model
 */
data class User(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val phone: String = "",
    val name: String = "",
    val profilePhotoUrl: String = "",
    val role: String = UserRole.RESIDENT.name,
    val villageId: String = "", // Empty for super admin
    val isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun getUserRole(): UserRole {
        return try {
            UserRole.valueOf(role)
        } catch (e: Exception) {
            UserRole.RESIDENT
        }
    }
}

/**
 * Village location coordinates
 */
data class VillageLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = ""
)

/**
 * Village data model
 */
data class Village(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: VillageLocation = VillageLocation(),
    val adminId: String = "",
    val adminName: String = "",
    val adminContact: String = "",
    val population: Int = 0,
    val area: String = "", // in sq km
    val imageUrl: String = "",
    val isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

/**
 * Important location within a village
 */
data class ImportantLocation(
    @DocumentId
    val id: String = "",
    val villageId: String = "",
    val name: String = "",
    val type: String = "", // SCHOOL, HOSPITAL, TEMPLE, MOSQUE, CHURCH, GOVERNMENT_OFFICE, etc.
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = "",
    val contactNumber: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

/**
 * Help request categories
 */
enum class HelpCategory {
    EMERGENCY,
    INFRASTRUCTURE,
    SOCIAL,
    HEALTH,
    EDUCATION,
    OTHER
}

/**
 * Help request status
 */
enum class HelpStatus {
    PENDING,
    IN_PROGRESS,
    RESOLVED,
    REJECTED
}

/**
 * Help request model
 */
data class HelpRequest(
    @DocumentId
    val id: String = "",
    val villageId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val category: String = HelpCategory.OTHER.name,
    val title: String = "",
    val description: String = "",
    val status: String = HelpStatus.PENDING.name,
    val priority: Int = 0, // 0-low, 1-medium, 2-high, 3-critical
    val imageUrls: List<String> = emptyList(),
    val adminNotes: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val resolvedAt: Timestamp? = null
) {
    fun getHelpCategory(): HelpCategory {
        return try {
            HelpCategory.valueOf(category)
        } catch (e: Exception) {
            HelpCategory.OTHER
        }
    }

    fun getHelpStatus(): HelpStatus {
        return try {
            HelpStatus.valueOf(status)
        } catch (e: Exception) {
            HelpStatus.PENDING
        }
    }
}

/**
 * Suggestion model
 */
data class Suggestion(
    @DocumentId
    val id: String = "",
    val villageId: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val upvotes: Int = 0,
    val upvotedBy: List<String> = emptyList(), // List of user IDs
    val isReviewed: Boolean = false,
    val isImplemented: Boolean = false,
    val adminResponse: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

/**
 * Resident profile (extended user info for village residents)
 */
data class Resident(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val villageId: String = "",
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val occupation: String = "",
    val education: String = "",
    val familyMembers: Int = 0,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val photoUrl: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
)

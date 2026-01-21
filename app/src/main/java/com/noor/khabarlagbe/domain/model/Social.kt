package com.noor.khabarlagbe.domain.model

data class GroupOrder(
    val id: String,
    val hostUserId: String,
    val hostName: String,
    val restaurantId: String,
    val restaurantName: String,
    val status: GroupOrderStatus,
    val participants: List<GroupOrderParticipant>,
    val deadline: Long,
    val deliveryAddress: Address? = null,
    val shareCode: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class GroupOrderStatus {
    OPEN,
    LOCKED,
    SUBMITTED,
    CONFIRMED,
    DELIVERED,
    CANCELLED
}

data class GroupOrderParticipant(
    val userId: String,
    val userName: String,
    val profileImageUrl: String? = null,
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val hasConfirmed: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis()
)

data class Referral(
    val id: String,
    val referrerId: String,
    val referralCode: String,
    val referredUserId: String? = null,
    val referredUserName: String? = null,
    val status: ReferralStatus,
    val rewardPoints: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

enum class ReferralStatus {
    PENDING,
    SIGNED_UP,
    FIRST_ORDER,
    COMPLETED,
    EXPIRED
}

data class FriendActivity(
    val id: String,
    val userId: String,
    val userName: String,
    val userImageUrl: String? = null,
    val activityType: FriendActivityType,
    val restaurantId: String? = null,
    val restaurantName: String? = null,
    val itemName: String? = null,
    val rating: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class FriendActivityType {
    ORDERED_FROM,
    REVIEWED,
    FAVORITED,
    EARNED_REWARD,
    REACHED_TIER
}

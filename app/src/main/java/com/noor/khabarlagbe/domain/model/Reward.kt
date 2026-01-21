package com.noor.khabarlagbe.domain.model

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val imageUrl: String? = null,
    val expiryDate: Long? = null,
    val category: RewardCategory,
    val isRedeemed: Boolean = false,
    val redemptionCode: String? = null
)

enum class RewardCategory {
    FREE_DELIVERY,
    DISCOUNT_PERCENTAGE,
    DISCOUNT_FLAT,
    FREE_ITEM,
    CASHBACK,
    EXCLUSIVE_ACCESS
}

data class LoyaltyProfile(
    val userId: String,
    val currentPoints: Int,
    val lifetimePoints: Int,
    val tier: LoyaltyTier,
    val tierProgress: Float, // 0.0 to 1.0
    val pointsToNextTier: Int,
    val memberSince: Long,
    val streakDays: Int = 0,
    val availableRewards: List<Reward> = emptyList()
)

enum class LoyaltyTier(val displayName: String, val minPoints: Int, val multiplier: Float) {
    BRONZE("Bronze", 0, 1.0f),
    SILVER("Silver", 500, 1.25f),
    GOLD("Gold", 2000, 1.5f),
    PLATINUM("Platinum", 5000, 2.0f)
}

data class PointsTransaction(
    val id: String,
    val points: Int,
    val type: PointsTransactionType,
    val description: String,
    val orderId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class PointsTransactionType {
    EARNED_ORDER,
    EARNED_REFERRAL,
    EARNED_BONUS,
    EARNED_REVIEW,
    REDEEMED,
    EXPIRED,
    ADJUSTED
}

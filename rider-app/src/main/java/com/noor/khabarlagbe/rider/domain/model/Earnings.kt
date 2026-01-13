package com.noor.khabarlagbe.rider.domain.model

data class Earnings(
    val today: EarningsSummary,
    val thisWeek: EarningsSummary,
    val thisMonth: EarningsSummary,
    val history: List<EarningsEntry>
)

data class EarningsSummary(
    val totalEarnings: Double,
    val deliveryFees: Double,
    val tips: Double,
    val incentives: Double,
    val deliveriesCount: Int
)

data class EarningsEntry(
    val date: Long,
    val orderId: String,
    val amount: Double,
    val tip: Double,
    val type: EarningsType
)

enum class EarningsType {
    DELIVERY,
    TIP,
    INCENTIVE,
    BONUS
}

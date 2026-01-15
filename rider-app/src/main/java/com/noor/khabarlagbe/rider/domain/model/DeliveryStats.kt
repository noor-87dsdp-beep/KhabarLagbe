package com.noor.khabarlagbe.rider.domain.model

data class DeliveryStats(
    val totalDeliveries: Int = 0,
    val todayDeliveries: Int = 0,
    val weeklyDeliveries: Int = 0,
    val monthlyDeliveries: Int = 0,
    val avgRating: Double = 0.0,
    val acceptanceRate: Double = 0.0,
    val completionRate: Double = 0.0,
    val totalEarnings: Double = 0.0,
    val onTimeDeliveryRate: Double = 0.0,
    val averageDeliveryTime: Int = 0, // in minutes
    val weeklyTrend: List<DailyDeliveries> = emptyList()
)

data class DailyDeliveries(
    val date: String,
    val count: Int,
    val earnings: Double
)

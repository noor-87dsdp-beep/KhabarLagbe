package com.noor.khabarlagbe.rider.data.dto

import com.google.gson.annotations.SerializedName

data class StatsDto(
    @SerializedName("totalDeliveries") val totalDeliveries: Int,
    @SerializedName("completedDeliveries") val completedDeliveries: Int,
    @SerializedName("cancelledDeliveries") val cancelledDeliveries: Int,
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("totalRatings") val totalRatings: Int,
    @SerializedName("fiveStarRatings") val fiveStarRatings: Int,
    @SerializedName("acceptanceRate") val acceptanceRate: Double,
    @SerializedName("completionRate") val completionRate: Double,
    @SerializedName("onTimeRate") val onTimeRate: Double,
    @SerializedName("averageDeliveryTime") val averageDeliveryTime: Int,
    @SerializedName("totalDistance") val totalDistance: Double,
    @SerializedName("totalEarnings") val totalEarnings: Double,
    @SerializedName("peakHours") val peakHours: List<PeakHourDto>,
    @SerializedName("dailyStats") val dailyStats: List<DailyStatDto>,
    @SerializedName("leaderboardPosition") val leaderboardPosition: Int?
)

data class PeakHourDto(
    @SerializedName("hour") val hour: Int,
    @SerializedName("deliveries") val deliveries: Int,
    @SerializedName("earnings") val earnings: Double
)

data class DailyStatDto(
    @SerializedName("date") val date: String,
    @SerializedName("deliveries") val deliveries: Int,
    @SerializedName("earnings") val earnings: Double,
    @SerializedName("distance") val distance: Double,
    @SerializedName("onlineHours") val onlineHours: Double
)

data class LeaderboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("leaderboard") val leaderboard: List<LeaderboardEntryDto>,
    @SerializedName("myPosition") val myPosition: Int,
    @SerializedName("myStats") val myStats: LeaderboardEntryDto?
)

data class LeaderboardEntryDto(
    @SerializedName("position") val position: Int,
    @SerializedName("riderId") val riderId: String,
    @SerializedName("riderName") val riderName: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("deliveries") val deliveries: Int,
    @SerializedName("rating") val rating: Double,
    @SerializedName("earnings") val earnings: Double?
)

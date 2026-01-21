package com.noor.khabarlagbe.restaurant.data.dto

import com.google.gson.annotations.SerializedName

data class RestaurantDto(
    @SerializedName("id") val id: String,
    @SerializedName("businessName") val businessName: String,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("cuisineTypes") val cuisineTypes: List<String>,
    @SerializedName("rating") val rating: Double,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("isOpen") val isOpen: Boolean,
    @SerializedName("isBusy") val isBusy: Boolean,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("coverImageUrl") val coverImageUrl: String?,
    @SerializedName("operatingHours") val operatingHours: OperatingHoursDto?,
    @SerializedName("deliverySettings") val deliverySettings: DeliverySettingsDto?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class OperatingHoursDto(
    @SerializedName("monday") val monday: DayHoursDto?,
    @SerializedName("tuesday") val tuesday: DayHoursDto?,
    @SerializedName("wednesday") val wednesday: DayHoursDto?,
    @SerializedName("thursday") val thursday: DayHoursDto?,
    @SerializedName("friday") val friday: DayHoursDto?,
    @SerializedName("saturday") val saturday: DayHoursDto?,
    @SerializedName("sunday") val sunday: DayHoursDto?
)

data class DayHoursDto(
    @SerializedName("isOpen") val isOpen: Boolean,
    @SerializedName("openTime") val openTime: String,
    @SerializedName("closeTime") val closeTime: String
)

data class DeliverySettingsDto(
    @SerializedName("minimumOrder") val minimumOrder: Double,
    @SerializedName("deliveryRadius") val deliveryRadius: Double,
    @SerializedName("estimatedPrepTime") val estimatedPrepTime: Int,
    @SerializedName("packagingCharge") val packagingCharge: Double
)

data class RestaurantUpdateRequestDto(
    @SerializedName("businessName") val businessName: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("address") val address: AddressDto? = null,
    @SerializedName("cuisineTypes") val cuisineTypes: List<String>? = null,
    @SerializedName("isOpen") val isOpen: Boolean? = null,
    @SerializedName("isBusy") val isBusy: Boolean? = null,
    @SerializedName("operatingHours") val operatingHours: OperatingHoursDto? = null,
    @SerializedName("deliverySettings") val deliverySettings: DeliverySettingsDto? = null
)

data class RestaurantResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: RestaurantDto?
)

data class RestaurantStatsDto(
    @SerializedName("todayOrders") val todayOrders: Int,
    @SerializedName("todayRevenue") val todayRevenue: Double,
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("pendingOrders") val pendingOrders: Int,
    @SerializedName("preparingOrders") val preparingOrders: Int,
    @SerializedName("readyOrders") val readyOrders: Int,
    @SerializedName("completedOrders") val completedOrders: Int
)

data class RestaurantStatsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: RestaurantStatsDto?
)

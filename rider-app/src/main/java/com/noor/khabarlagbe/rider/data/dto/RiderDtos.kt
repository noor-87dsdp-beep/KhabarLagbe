package com.noor.khabarlagbe.rider.data.dto

import com.google.gson.annotations.SerializedName
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.VehicleType
import com.noor.khabarlagbe.rider.domain.model.Location

data class RiderDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String?,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("vehicleType") val vehicleType: String,
    @SerializedName("vehicleNumber") val vehicleNumber: String,
    @SerializedName("licenseNumber") val licenseNumber: String,
    @SerializedName("nidNumber") val nidNumber: String,
    @SerializedName("isOnline") val isOnline: Boolean,
    @SerializedName("isVerified") val isVerified: Boolean,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("rating") val rating: Double,
    @SerializedName("totalDeliveries") val totalDeliveries: Int,
    @SerializedName("todayEarnings") val todayEarnings: Double,
    @SerializedName("weeklyEarnings") val weeklyEarnings: Double,
    @SerializedName("monthlyEarnings") val monthlyEarnings: Double,
    @SerializedName("currentLatitude") val currentLatitude: Double?,
    @SerializedName("currentLongitude") val currentLongitude: Double?,
    @SerializedName("bankAccountName") val bankAccountName: String?,
    @SerializedName("bankAccountNumber") val bankAccountNumber: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("bikashNumber") val bikashNumber: String?,
    @SerializedName("nagadNumber") val nagadNumber: String?,
    @SerializedName("documents") val documents: List<DocumentDto>?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
) {
    fun toDomain(): Rider {
        return Rider(
            id = id,
            name = name,
            phone = phone,
            email = email,
            profileImageUrl = profileImageUrl,
            vehicleType = try { VehicleType.valueOf(vehicleType.uppercase()) } catch (e: Exception) { VehicleType.MOTORCYCLE },
            vehicleNumber = vehicleNumber,
            licenseNumber = licenseNumber,
            nidNumber = nidNumber,
            isOnline = isOnline,
            rating = rating,
            totalDeliveries = totalDeliveries,
            todayEarnings = todayEarnings,
            weeklyEarnings = weeklyEarnings,
            monthlyEarnings = monthlyEarnings,
            currentLocation = if (currentLatitude != null && currentLongitude != null) {
                Location(currentLatitude, currentLongitude)
            } else null
        )
    }
}

data class DocumentDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("status") val status: String,
    @SerializedName("uploadedAt") val uploadedAt: String
)

// Profile Update Requests
data class UpdateProfileRequest(
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?
)

data class UpdateVehicleRequest(
    @SerializedName("vehicleType") val vehicleType: String,
    @SerializedName("vehicleNumber") val vehicleNumber: String,
    @SerializedName("licenseNumber") val licenseNumber: String
)

data class UpdateBankRequest(
    @SerializedName("bankAccountName") val bankAccountName: String?,
    @SerializedName("bankAccountNumber") val bankAccountNumber: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("bikashNumber") val bikashNumber: String?,
    @SerializedName("nagadNumber") val nagadNumber: String?
)

data class UpdateStatusRequest(
    @SerializedName("isOnline") val isOnline: Boolean
)

data class UpdateLocationRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("bearing") val bearing: Float? = null,
    @SerializedName("speed") val speed: Float? = null
)

// Upload Responses
data class PhotoUploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("url") val url: String,
    @SerializedName("message") val message: String?
)

data class DocumentUploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("document") val document: DocumentDto,
    @SerializedName("message") val message: String?
)

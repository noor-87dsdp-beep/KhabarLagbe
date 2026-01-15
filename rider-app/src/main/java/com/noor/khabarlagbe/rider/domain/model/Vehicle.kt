package com.noor.khabarlagbe.rider.domain.model

data class Vehicle(
    val id: String? = null,
    val type: VehicleType,
    val make: String,
    val model: String,
    val plateNumber: String,
    val registrationNumber: String,
    val year: Int? = null,
    val color: String? = null,
    val registrationDocUrl: String? = null
)

data class RiderDocument(
    val id: String? = null,
    val type: DocumentType,
    val documentNumber: String,
    val documentUrl: String? = null,
    val expiryDate: Long? = null,
    val isVerified: Boolean = false
)

enum class DocumentType {
    NID,
    DRIVING_LICENSE,
    VEHICLE_REGISTRATION,
    PROFILE_PHOTO
}

package com.noor.khabarlagbe.rider.data.dto

import com.google.gson.annotations.SerializedName

// Auth Request DTOs
data class LoginRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String,
    @SerializedName("vehicleType") val vehicleType: String,
    @SerializedName("vehicleNumber") val vehicleNumber: String,
    @SerializedName("licenseNumber") val licenseNumber: String,
    @SerializedName("nidNumber") val nidNumber: String,
    @SerializedName("bankAccountName") val bankAccountName: String?,
    @SerializedName("bankAccountNumber") val bankAccountNumber: String?,
    @SerializedName("bankName") val bankName: String?,
    @SerializedName("bikashNumber") val bikashNumber: String?,
    @SerializedName("nagadNumber") val nagadNumber: String?
)

data class VerifyOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("otp") val otp: String
)

data class ResendOtpRequest(
    @SerializedName("phone") val phone: String
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

// Auth Response DTOs
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("rider") val rider: RiderDto?,
    @SerializedName("accessToken") val accessToken: String?,
    @SerializedName("refreshToken") val refreshToken: String?,
    @SerializedName("requiresOtp") val requiresOtp: Boolean = false
)

data class MessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class FcmTokenRequest(
    @SerializedName("token") val token: String
)

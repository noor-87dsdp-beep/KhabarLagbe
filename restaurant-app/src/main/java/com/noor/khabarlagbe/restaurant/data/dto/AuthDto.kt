package com.noor.khabarlagbe.restaurant.data.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: AuthDataDto?
)

data class AuthDataDto(
    @SerializedName("token") val token: String,
    @SerializedName("restaurant") val restaurant: RestaurantDto
)

data class RegisterRequestDto(
    @SerializedName("businessName") val businessName: String,
    @SerializedName("ownerName") val ownerName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("address") val address: AddressDto,
    @SerializedName("cuisineTypes") val cuisineTypes: List<String>,
    @SerializedName("documents") val documents: DocumentsDto,
    @SerializedName("bankDetails") val bankDetails: BankDetailsDto
)

data class RegisterResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: AuthDataDto?
)

data class AddressDto(
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("area") val area: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class DocumentsDto(
    @SerializedName("tradeLicense") val tradeLicense: String,
    @SerializedName("nidFront") val nidFront: String,
    @SerializedName("nidBack") val nidBack: String,
    @SerializedName("restaurantPhoto") val restaurantPhoto: String
)

data class BankDetailsDto(
    @SerializedName("bankName") val bankName: String,
    @SerializedName("accountName") val accountName: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("branchName") val branchName: String,
    @SerializedName("routingNumber") val routingNumber: String?
)

data class RefreshTokenRequestDto(
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshTokenResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: TokenDataDto?
)

data class TokenDataDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

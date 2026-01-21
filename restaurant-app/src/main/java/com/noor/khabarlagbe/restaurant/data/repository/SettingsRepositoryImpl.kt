package com.noor.khabarlagbe.restaurant.data.repository

import com.noor.khabarlagbe.restaurant.data.api.RestaurantApi
import com.noor.khabarlagbe.restaurant.data.dto.*
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import com.noor.khabarlagbe.restaurant.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val api: RestaurantApi,
    private val authRepository: RestaurantAuthRepository
) : SettingsRepository {
    
    private fun getAuthToken(): String {
        return "Bearer ${authRepository.getToken() ?: ""}"
    }
    
    override suspend fun getRestaurantProfile(): Result<Restaurant> {
        return try {
            val response = api.getProfile(getAuthToken())
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurant = response.body()!!.data!!.toDomain()
                authRepository.saveRestaurant(restaurant)
                Result.success(restaurant)
            } else {
                Result.failure(Exception("Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateRestaurantProfile(restaurant: Restaurant): Result<Restaurant> {
        return try {
            val request = RestaurantUpdateRequestDto(
                businessName = restaurant.businessName,
                phone = restaurant.phone,
                address = AddressDto(
                    street = restaurant.address.street,
                    city = restaurant.address.city,
                    area = restaurant.address.area,
                    postalCode = restaurant.address.postalCode,
                    latitude = restaurant.address.latitude,
                    longitude = restaurant.address.longitude
                ),
                cuisineTypes = restaurant.cuisineTypes,
                operatingHours = restaurant.operatingHours?.toDto(),
                deliverySettings = restaurant.deliverySettings?.toDto()
            )
            val response = api.updateProfile(getAuthToken(), request)
            if (response.isSuccessful && response.body()?.success == true) {
                val updated = response.body()!!.data!!.toDomain()
                authRepository.saveRestaurant(updated)
                Result.success(updated)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateOpenStatus(isOpen: Boolean): Result<Restaurant> {
        return try {
            val response = api.updateStatus(getAuthToken(), mapOf("isOpen" to isOpen))
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurant = response.body()!!.data!!.toDomain()
                authRepository.saveRestaurant(restaurant)
                Result.success(restaurant)
            } else {
                Result.failure(Exception("Failed to update status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBusyMode(isBusy: Boolean): Result<Restaurant> {
        return try {
            val response = api.updateStatus(getAuthToken(), mapOf("isBusy" to isBusy))
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurant = response.body()!!.data!!.toDomain()
                authRepository.saveRestaurant(restaurant)
                Result.success(restaurant)
            } else {
                Result.failure(Exception("Failed to update busy mode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateOperatingHours(hours: OperatingHours): Result<Restaurant> {
        return try {
            val request = RestaurantUpdateRequestDto(operatingHours = hours.toDto())
            val response = api.updateProfile(getAuthToken(), request)
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurant = response.body()!!.data!!.toDomain()
                authRepository.saveRestaurant(restaurant)
                Result.success(restaurant)
            } else {
                Result.failure(Exception("Failed to update operating hours"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDeliverySettings(settings: DeliverySettings): Result<Restaurant> {
        return try {
            val request = RestaurantUpdateRequestDto(deliverySettings = settings.toDto())
            val response = api.updateProfile(getAuthToken(), request)
            if (response.isSuccessful && response.body()?.success == true) {
                val restaurant = response.body()!!.data!!.toDomain()
                authRepository.saveRestaurant(restaurant)
                Result.success(restaurant)
            } else {
                Result.failure(Exception("Failed to update delivery settings"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun registerFcmToken(token: String): Result<Unit> {
        return try {
            val response = api.registerDevice(getAuthToken(), mapOf("fcmToken" to token, "platform" to "android"))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to register device"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun RestaurantDto.toDomain(): Restaurant {
        return Restaurant(
            id = id,
            businessName = businessName,
            ownerName = ownerName,
            email = email,
            phone = phone,
            address = Address(
                street = address.street,
                city = address.city,
                area = address.area,
                postalCode = address.postalCode,
                latitude = address.latitude,
                longitude = address.longitude
            ),
            cuisineTypes = cuisineTypes,
            rating = rating,
            totalReviews = totalReviews,
            isOpen = isOpen,
            isBusy = isBusy,
            imageUrl = imageUrl,
            coverImageUrl = coverImageUrl,
            operatingHours = operatingHours?.toDomain(),
            deliverySettings = deliverySettings?.toDomain()
        )
    }
    
    private fun OperatingHoursDto.toDomain(): OperatingHours {
        return OperatingHours(
            monday = monday?.toDomain(),
            tuesday = tuesday?.toDomain(),
            wednesday = wednesday?.toDomain(),
            thursday = thursday?.toDomain(),
            friday = friday?.toDomain(),
            saturday = saturday?.toDomain(),
            sunday = sunday?.toDomain()
        )
    }
    
    private fun DayHoursDto.toDomain(): DayHours {
        return DayHours(isOpen = isOpen, openTime = openTime, closeTime = closeTime)
    }
    
    private fun DeliverySettingsDto.toDomain(): DeliverySettings {
        return DeliverySettings(
            minimumOrder = minimumOrder,
            deliveryRadius = deliveryRadius,
            estimatedPrepTime = estimatedPrepTime,
            packagingCharge = packagingCharge
        )
    }
    
    private fun OperatingHours.toDto(): OperatingHoursDto {
        return OperatingHoursDto(
            monday = monday?.toDto(),
            tuesday = tuesday?.toDto(),
            wednesday = wednesday?.toDto(),
            thursday = thursday?.toDto(),
            friday = friday?.toDto(),
            saturday = saturday?.toDto(),
            sunday = sunday?.toDto()
        )
    }
    
    private fun DayHours.toDto(): DayHoursDto {
        return DayHoursDto(isOpen = isOpen, openTime = openTime, closeTime = closeTime)
    }
    
    private fun DeliverySettings.toDto(): DeliverySettingsDto {
        return DeliverySettingsDto(
            minimumOrder = minimumOrder,
            deliveryRadius = deliveryRadius,
            estimatedPrepTime = estimatedPrepTime,
            packagingCharge = packagingCharge
        )
    }
}

package com.noor.khabarlagbe.restaurant.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.noor.khabarlagbe.restaurant.data.api.RestaurantApi
import com.noor.khabarlagbe.restaurant.data.dto.*
import com.noor.khabarlagbe.restaurant.data.local.dao.RestaurantDao
import com.noor.khabarlagbe.restaurant.data.local.entity.RestaurantEntity
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "restaurant_prefs")

@Singleton
class RestaurantAuthRepositoryImpl @Inject constructor(
    private val api: RestaurantApi,
    private val restaurantDao: RestaurantDao,
    @ApplicationContext private val context: Context
) : RestaurantAuthRepository {
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val RESTAURANT_ID_KEY = stringPreferencesKey("restaurant_id")
    }
    
    override suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val response = api.login(LoginRequestDto(email, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                saveToken(data.token)
                val restaurant = data.restaurant.toDomain()
                saveRestaurant(restaurant)
                Result.success(AuthResult(data.token, restaurant))
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(data: RegistrationData): Result<AuthResult> {
        return try {
            val request = RegisterRequestDto(
                businessName = data.businessName,
                ownerName = data.ownerName,
                email = data.email,
                phone = data.phone,
                password = data.password,
                address = AddressDto(
                    street = data.address.street,
                    city = data.address.city,
                    area = data.address.area,
                    postalCode = data.address.postalCode,
                    latitude = data.address.latitude,
                    longitude = data.address.longitude
                ),
                cuisineTypes = data.cuisineTypes,
                documents = DocumentsDto(
                    tradeLicense = data.documents.tradeLicense,
                    nidFront = data.documents.nidFront,
                    nidBack = data.documents.nidBack,
                    restaurantPhoto = data.documents.restaurantPhoto
                ),
                bankDetails = BankDetailsDto(
                    bankName = data.bankDetails.bankName,
                    accountName = data.bankDetails.accountName,
                    accountNumber = data.bankDetails.accountNumber,
                    branchName = data.bankDetails.branchName,
                    routingNumber = data.bankDetails.routingNumber
                )
            )
            val response = api.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val responseData = response.body()!!.data!!
                saveToken(responseData.token)
                val restaurant = responseData.restaurant.toDomain()
                saveRestaurant(restaurant)
                Result.success(AuthResult(responseData.token, restaurant))
            } else {
                Result.failure(Exception(response.body()?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            val token = getToken() ?: return Result.success(Unit)
            api.logout("Bearer $token")
            clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            clearSession()
            Result.success(Unit)
        }
    }
    
    override suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = context.dataStore.data.first()[REFRESH_TOKEN_KEY]
                ?: return Result.failure(Exception("No refresh token"))
            val response = api.refreshToken(RefreshTokenRequestDto(refreshToken))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                saveToken(data.accessToken)
                context.dataStore.edit { prefs ->
                    prefs[REFRESH_TOKEN_KEY] = data.refreshToken
                }
                Result.success(data.accessToken)
            } else {
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = api.forgotPassword(mapOf("email" to email))
            if (response.isSuccessful) {
                Result.success("Password reset email sent")
            } else {
                Result.failure(Exception("Failed to send reset email"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun isLoggedIn(): Boolean {
        return runCatching {
            kotlinx.coroutines.runBlocking {
                context.dataStore.data.first()[TOKEN_KEY] != null
            }
        }.getOrDefault(false)
    }
    
    override fun getToken(): String? {
        return runCatching {
            kotlinx.coroutines.runBlocking {
                context.dataStore.data.first()[TOKEN_KEY]
            }
        }.getOrNull()
    }
    
    override suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }
    
    override suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
            prefs.remove(RESTAURANT_ID_KEY)
        }
        restaurantDao.clearRestaurants()
    }
    
    override fun getCurrentRestaurant(): Flow<Restaurant?> {
        return restaurantDao.getRestaurant(
            runCatching {
                kotlinx.coroutines.runBlocking {
                    context.dataStore.data.first()[RESTAURANT_ID_KEY] ?: ""
                }
            }.getOrDefault("")
        ).map { it?.toDomain() }
    }
    
    override suspend fun saveRestaurant(restaurant: Restaurant) {
        context.dataStore.edit { prefs ->
            prefs[RESTAURANT_ID_KEY] = restaurant.id
        }
        restaurantDao.insertRestaurant(restaurant.toEntity())
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
    
    private fun Restaurant.toEntity(): RestaurantEntity {
        return RestaurantEntity(
            id = id,
            businessName = businessName,
            ownerName = ownerName,
            email = email,
            phone = phone,
            street = address.street,
            city = address.city,
            area = address.area,
            postalCode = address.postalCode,
            latitude = address.latitude,
            longitude = address.longitude,
            cuisineTypes = cuisineTypes,
            rating = rating,
            totalReviews = totalReviews,
            isOpen = isOpen,
            isBusy = isBusy,
            imageUrl = imageUrl,
            coverImageUrl = coverImageUrl,
            minimumOrder = deliverySettings?.minimumOrder,
            deliveryRadius = deliverySettings?.deliveryRadius,
            estimatedPrepTime = deliverySettings?.estimatedPrepTime,
            packagingCharge = deliverySettings?.packagingCharge,
            createdAt = "",
            updatedAt = ""
        )
    }
    
    private fun RestaurantEntity.toDomain(): Restaurant {
        return Restaurant(
            id = id,
            businessName = businessName,
            ownerName = ownerName,
            email = email,
            phone = phone,
            address = Address(street, city, area, postalCode, latitude, longitude),
            cuisineTypes = cuisineTypes,
            rating = rating,
            totalReviews = totalReviews,
            isOpen = isOpen,
            isBusy = isBusy,
            imageUrl = imageUrl,
            coverImageUrl = coverImageUrl,
            operatingHours = null,
            deliverySettings = if (minimumOrder != null) DeliverySettings(
                minimumOrder = minimumOrder,
                deliveryRadius = deliveryRadius ?: 5.0,
                estimatedPrepTime = estimatedPrepTime ?: 30,
                packagingCharge = packagingCharge ?: 0.0
            ) else null
        )
    }
}

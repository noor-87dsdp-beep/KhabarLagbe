package com.noor.khabarlagbe.rider.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.noor.khabarlagbe.rider.data.api.RiderApi
import com.noor.khabarlagbe.rider.data.dto.*
import com.noor.khabarlagbe.rider.data.local.dao.RiderDao
import com.noor.khabarlagbe.rider.data.local.entity.RiderEntity
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rider_prefs")

@Singleton
class RiderAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val riderApi: RiderApi,
    private val riderDao: RiderDao
) : RiderAuthRepository {
    
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_RIDER_ID = stringPreferencesKey("rider_id")
    }
    
    override val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }
    
    override val currentRider: Flow<Rider?> = riderDao.getCurrentRider().map { it?.toDomain() }
    
    override suspend fun login(phone: String, password: String): Result<Rider> {
        return try {
            val response = riderApi.login(LoginRequest(phone, password))
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                val riderDto = authResponse.rider
                
                if (riderDto != null && authResponse.accessToken != null) {
                    saveTokens(authResponse.accessToken, authResponse.refreshToken)
                    
                    val rider = riderDto.toDomain()
                    val entity = RiderEntity.fromDomain(rider).copy(
                        bankAccountName = riderDto.bankAccountName,
                        bankAccountNumber = riderDto.bankAccountNumber,
                        bankName = riderDto.bankName,
                        bikashNumber = riderDto.bikashNumber,
                        nagadNumber = riderDto.nagadNumber
                    )
                    riderDao.insertRider(entity)
                    
                    context.dataStore.edit { prefs ->
                        prefs[KEY_IS_LOGGED_IN] = true
                        prefs[KEY_RIDER_ID] = rider.id
                    }
                    
                    Result.success(rider)
                } else if (authResponse.requiresOtp) {
                    Result.failure(OtpRequiredException("OTP verification required"))
                } else {
                    Result.failure(Exception(authResponse.message ?: "Login failed"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(
        name: String,
        phone: String,
        email: String?,
        password: String,
        vehicleType: String,
        vehicleNumber: String,
        licenseNumber: String,
        nidNumber: String,
        bankAccountName: String?,
        bankAccountNumber: String?,
        bankName: String?,
        bikashNumber: String?,
        nagadNumber: String?
    ): Result<Rider> {
        return try {
            val request = RegisterRequest(
                name = name,
                phone = phone,
                email = email,
                password = password,
                vehicleType = vehicleType,
                vehicleNumber = vehicleNumber,
                licenseNumber = licenseNumber,
                nidNumber = nidNumber,
                bankAccountName = bankAccountName,
                bankAccountNumber = bankAccountNumber,
                bankName = bankName,
                bikashNumber = bikashNumber,
                nagadNumber = nagadNumber
            )
            
            val response = riderApi.register(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                
                if (authResponse.requiresOtp) {
                    Result.failure(OtpRequiredException("OTP verification required"))
                } else {
                    val riderDto = authResponse.rider
                    if (riderDto != null && authResponse.accessToken != null) {
                        saveTokens(authResponse.accessToken, authResponse.refreshToken)
                        
                        val rider = riderDto.toDomain()
                        val entity = RiderEntity.fromDomain(rider).copy(
                            bankAccountName = riderDto.bankAccountName,
                            bankAccountNumber = riderDto.bankAccountNumber,
                            bankName = riderDto.bankName,
                            bikashNumber = riderDto.bikashNumber,
                            nagadNumber = riderDto.nagadNumber
                        )
                        riderDao.insertRider(entity)
                        
                        context.dataStore.edit { prefs ->
                            prefs[KEY_IS_LOGGED_IN] = true
                            prefs[KEY_RIDER_ID] = rider.id
                        }
                        
                        Result.success(rider)
                    } else {
                        Result.failure(Exception(authResponse.message ?: "Registration failed"))
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyOtp(phone: String, otp: String): Result<Rider> {
        return try {
            val response = riderApi.verifyOtp(VerifyOtpRequest(phone, otp))
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                val riderDto = authResponse.rider
                
                if (riderDto != null && authResponse.accessToken != null) {
                    saveTokens(authResponse.accessToken, authResponse.refreshToken)
                    
                    val rider = riderDto.toDomain()
                    val entity = RiderEntity.fromDomain(rider).copy(
                        bankAccountName = riderDto.bankAccountName,
                        bankAccountNumber = riderDto.bankAccountNumber,
                        bankName = riderDto.bankName,
                        bikashNumber = riderDto.bikashNumber,
                        nagadNumber = riderDto.nagadNumber
                    )
                    riderDao.insertRider(entity)
                    
                    context.dataStore.edit { prefs ->
                        prefs[KEY_IS_LOGGED_IN] = true
                        prefs[KEY_RIDER_ID] = rider.id
                    }
                    
                    Result.success(rider)
                } else {
                    Result.failure(Exception(authResponse.message ?: "OTP verification failed"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "OTP verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resendOtp(phone: String): Result<Unit> {
        return try {
            val response = riderApi.resendOtp(ResendOtpRequest(phone))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception(errorBody ?: "Failed to resend OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            val token = getAccessToken()
            if (token != null) {
                riderApi.logout("Bearer $token")
            }
            clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            clearSession()
            Result.success(Unit)
        }
    }
    
    override suspend fun refreshToken(): Result<Unit> {
        return try {
            val refreshToken = context.dataStore.data.first()[KEY_REFRESH_TOKEN]
            if (refreshToken != null) {
                val response = riderApi.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful && response.body()?.success == true) {
                    val authResponse = response.body()!!
                    if (authResponse.accessToken != null) {
                        saveTokens(authResponse.accessToken, authResponse.refreshToken)
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Failed to refresh token"))
                    }
                } else {
                    Result.failure(Exception("Failed to refresh token"))
                }
            } else {
                Result.failure(Exception("No refresh token available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAccessToken(): String? {
        return context.dataStore.data.first()[KEY_ACCESS_TOKEN]
    }
    
    override suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
        }
    }
    
    override suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_RIDER_ID)
            prefs[KEY_IS_LOGGED_IN] = false
        }
        riderDao.clearAll()
    }
    
    override suspend fun registerFcmToken(token: String): Result<Unit> {
        return try {
            val accessToken = getAccessToken()
            if (accessToken != null) {
                val response = riderApi.registerFcmToken(
                    "Bearer $accessToken",
                    FcmTokenRequest(token)
                )
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to register FCM token"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            refreshToken?.let { prefs[KEY_REFRESH_TOKEN] = it }
        }
    }
}

class OtpRequiredException(message: String) : Exception(message)

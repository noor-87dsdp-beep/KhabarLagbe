package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.remote.api.LoginRequest
import com.noor.khabarlagbe.rider.data.remote.api.OnlineStatusRequest
import com.noor.khabarlagbe.rider.data.remote.api.RegisterRequest
import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderAuthRepositoryImpl @Inject constructor(
    private val api: RiderApi
) : RiderAuthRepository {
    
    private val _authState = MutableStateFlow<Rider?>(null)
    private var authToken: String? = null
    
    override suspend fun login(phone: String, password: String): Result<Rider> {
        return try {
            val response = api.login(LoginRequest(phone, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                authToken = authResponse.token
                _authState.value = authResponse.rider
                Result.success(authResponse.rider)
            } else {
                Result.failure(Exception("লগইন ব্যর্থ হয়েছে"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(
        name: String,
        phone: String,
        email: String,
        password: String,
        vehicleType: String,
        vehicleMake: String,
        vehicleModel: String,
        plateNumber: String,
        nidNumber: String,
        licenseNumber: String
    ): Result<Rider> {
        return try {
            val request = RegisterRequest(
                name, phone, email, password, vehicleType,
                vehicleMake, vehicleModel, plateNumber, nidNumber, licenseNumber
            )
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                authToken = authResponse.token
                _authState.value = authResponse.rider
                Result.success(authResponse.rider)
            } else {
                Result.failure(Exception("নিবন্ধন ব্যর্থ হয়েছে"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            authToken = null
            _authState.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentRider(): Result<Rider?> {
        return try {
            if (_authState.value != null) {
                return Result.success(_authState.value)
            }
            val response = api.getCurrentRider()
            if (response.isSuccessful) {
                _authState.value = response.body()
                Result.success(response.body())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeAuthState(): Flow<Rider?> = _authState.asStateFlow()
    
    override suspend fun updateOnlineStatus(isOnline: Boolean): Result<Unit> {
        return try {
            val response = api.updateOnlineStatus(OnlineStatusRequest(isOnline))
            if (response.isSuccessful) {
                _authState.value = _authState.value?.copy(isOnline = isOnline)
                Result.success(Unit)
            } else {
                Result.failure(Exception("স্ট্যাটাস আপডেট ব্যর্থ হয়েছে"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

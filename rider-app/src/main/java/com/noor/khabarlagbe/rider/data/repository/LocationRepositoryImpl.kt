package com.noor.khabarlagbe.rider.data.repository

import android.content.Context
import android.content.Intent
import com.noor.khabarlagbe.rider.data.api.RiderApi
import com.noor.khabarlagbe.rider.data.dto.UpdateLocationRequest
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import com.noor.khabarlagbe.rider.service.LocationService
import com.noor.khabarlagbe.rider.service.LocationUpdate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val riderApi: RiderApi,
    private val authRepository: RiderAuthRepository
) : LocationRepository {
    
    private val _isTracking = MutableStateFlow(false)
    override val isTracking: Flow<Boolean> = _isTracking.asStateFlow()
    
    override val locationUpdates: Flow<LocationUpdate> = LocationService.locationUpdates
    
    override suspend fun startLocationTracking() {
        if (!LocationService.isServiceRunning()) {
            val intent = Intent(context, LocationService::class.java)
            context.startForegroundService(intent)
            _isTracking.value = true
        }
    }
    
    override suspend fun stopLocationTracking() {
        if (LocationService.isServiceRunning()) {
            val intent = Intent(context, LocationService::class.java)
            context.stopService(intent)
            _isTracking.value = false
        }
    }
    
    override suspend fun getCurrentLocation(): LocationUpdate? {
        return try {
            locationUpdates.first()
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun updateLocationToServer(
        latitude: Double,
        longitude: Double,
        bearing: Float?,
        speed: Float?
    ): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.updateLocation(
                    "Bearer $token",
                    UpdateLocationRequest(latitude, longitude, bearing, speed)
                )
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to update location"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371.0 // Earth's radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
    
    override fun estimateTime(distanceKm: Double, speedKmh: Double): Int {
        if (speedKmh <= 0) return 0
        return ((distanceKm / speedKmh) * 60).toInt()
    }
}

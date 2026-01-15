package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.domain.model.Location
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val api: RiderApi
) : LocationRepository {
    
    private val _locationUpdates = MutableStateFlow<Location?>(null)
    private var isTracking = false
    private var lastKnownLocation: Location? = null
    
    override fun getLocationUpdates(): Flow<Location> {
        return _locationUpdates
            .map { it ?: Location(0.0, 0.0) }
    }
    
    override suspend fun sendLocationUpdate(location: Location): Result<Unit> {
        return try {
            lastKnownLocation = location
            _locationUpdates.value = location
            val response = api.sendLocationUpdate(location)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("লোকেশন আপডেট ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLastKnownLocation(): Location? = lastKnownLocation
    
    override suspend fun startLocationTracking(): Result<Unit> {
        isTracking = true
        return Result.success(Unit)
    }
    
    override suspend fun stopLocationTracking(): Result<Unit> {
        isTracking = false
        _locationUpdates.value = null
        return Result.success(Unit)
    }
}

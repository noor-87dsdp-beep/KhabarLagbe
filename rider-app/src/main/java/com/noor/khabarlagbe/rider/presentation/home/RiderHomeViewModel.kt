package com.noor.khabarlagbe.rider.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import com.noor.khabarlagbe.rider.service.LocationUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val rider: Rider?,
        val activeOrder: RiderOrder?,
        val todayEarnings: Double,
        val todayDeliveries: Int,
        val isOnline: Boolean,
        val currentLocation: LocationUpdate?
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class RiderHomeViewModel @Inject constructor(
    private val authRepository: RiderAuthRepository,
    private val orderRepository: RiderOrderRepository,
    private val earningsRepository: RiderEarningsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<LocationUpdate?>(null)
    val currentLocation: StateFlow<LocationUpdate?> = _currentLocation.asStateFlow()
    
    val currentRider = authRepository.currentRider
    val activeOrder = orderRepository.activeOrder
    val todayEarnings = earningsRepository.todayEarnings
    
    init {
        loadHomeData()
        observeLocation()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                combine(
                    authRepository.currentRider,
                    orderRepository.activeOrder,
                    earningsRepository.todayEarnings
                ) { rider, activeOrder, earnings ->
                    Triple(rider, activeOrder, earnings)
                }.collect { (rider, activeOrder, earnings) ->
                    _uiState.value = HomeUiState.Success(
                        rider = rider,
                        activeOrder = activeOrder,
                        todayEarnings = earnings,
                        todayDeliveries = rider?.totalDeliveries ?: 0,
                        isOnline = _isOnline.value,
                        currentLocation = _currentLocation.value
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load data")
            }
        }
    }
    
    private fun observeLocation() {
        viewModelScope.launch {
            locationRepository.locationUpdates.collect { location ->
                _currentLocation.value = location
                
                // Update location to server periodically
                if (_isOnline.value) {
                    locationRepository.updateLocationToServer(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        bearing = location.bearing,
                        speed = location.speed
                    )
                }
            }
        }
    }
    
    fun toggleOnlineStatus() {
        viewModelScope.launch {
            val newStatus = !_isOnline.value
            _isOnline.value = newStatus
            
            if (newStatus) {
                locationRepository.startLocationTracking()
            } else {
                locationRepository.stopLocationTracking()
            }
            
            // Update UI state
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isOnline = newStatus)
            }
        }
    }
    
    fun goOnline() {
        viewModelScope.launch {
            _isOnline.value = true
            locationRepository.startLocationTracking()
            
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isOnline = true)
            }
        }
    }
    
    fun goOffline() {
        viewModelScope.launch {
            _isOnline.value = false
            locationRepository.stopLocationTracking()
            
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isOnline = false)
            }
        }
    }
    
    fun refreshData() {
        viewModelScope.launch {
            orderRepository.syncOrders()
            earningsRepository.syncEarnings()
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            goOffline()
            authRepository.logout()
        }
    }
}

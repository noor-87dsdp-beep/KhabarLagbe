package com.noor.khabarlagbe.rider.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import com.noor.khabarlagbe.rider.service.LocationUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AvailableOrdersUiState {
    object Loading : AvailableOrdersUiState()
    object Empty : AvailableOrdersUiState()
    data class Success(
        val orders: List<RiderOrder>,
        val currentLocation: LocationUpdate?
    ) : AvailableOrdersUiState()
    data class Error(val message: String) : AvailableOrdersUiState()
}

@HiltViewModel
class AvailableOrdersViewModel @Inject constructor(
    private val orderRepository: RiderOrderRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AvailableOrdersUiState>(AvailableOrdersUiState.Loading)
    val uiState: StateFlow<AvailableOrdersUiState> = _uiState.asStateFlow()
    
    private val _availableOrders = MutableStateFlow<List<RiderOrder>>(emptyList())
    val availableOrders: StateFlow<List<RiderOrder>> = _availableOrders.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<LocationUpdate?>(null)
    val currentLocation: StateFlow<LocationUpdate?> = _currentLocation.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private var refreshJob: Job? = null
    
    init {
        observeLocation()
        startAutoRefresh()
    }
    
    private fun observeLocation() {
        viewModelScope.launch {
            locationRepository.locationUpdates.collect { location ->
                _currentLocation.value = location
                loadAvailableOrders(location.latitude, location.longitude)
            }
        }
    }
    
    private fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(30_000) // Refresh every 30 seconds
                val location = _currentLocation.value
                if (location != null) {
                    loadAvailableOrders(location.latitude, location.longitude, silent = true)
                }
            }
        }
    }
    
    fun loadAvailableOrders(latitude: Double, longitude: Double, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.value = AvailableOrdersUiState.Loading
            }
            
            val result = orderRepository.getAvailableOrders(latitude, longitude)
            result.fold(
                onSuccess = { orders ->
                    _availableOrders.value = orders
                    _uiState.value = if (orders.isEmpty()) {
                        AvailableOrdersUiState.Empty
                    } else {
                        AvailableOrdersUiState.Success(
                            orders = orders,
                            currentLocation = _currentLocation.value
                        )
                    }
                },
                onFailure = { exception ->
                    if (!silent) {
                        _uiState.value = AvailableOrdersUiState.Error(
                            exception.message ?: "Failed to load orders"
                        )
                    }
                }
            )
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val location = _currentLocation.value
            if (location != null) {
                loadAvailableOrders(location.latitude, location.longitude)
            }
            _isRefreshing.value = false
        }
    }
    
    fun acceptOrder(orderId: String, onSuccess: (RiderOrder) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = orderRepository.acceptOrder(orderId)
            result.fold(
                onSuccess = { order ->
                    _availableOrders.value = _availableOrders.value.filter { it.id != orderId }
                    onSuccess(order)
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Failed to accept order")
                }
            )
        }
    }
    
    fun rejectOrder(orderId: String, reason: String? = null) {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId, reason)
            _availableOrders.value = _availableOrders.value.filter { it.id != orderId }
        }
    }
    
    fun calculateDistance(order: RiderOrder): Double {
        val location = _currentLocation.value ?: return 0.0
        return locationRepository.calculateDistance(
            location.latitude,
            location.longitude,
            order.restaurantLocation.latitude,
            order.restaurantLocation.longitude
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}

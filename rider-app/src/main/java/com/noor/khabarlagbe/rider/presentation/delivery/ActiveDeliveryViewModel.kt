package com.noor.khabarlagbe.rider.presentation.delivery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import com.noor.khabarlagbe.rider.service.LocationUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActiveDeliveryUiState {
    object Loading : ActiveDeliveryUiState()
    object NoActiveDelivery : ActiveDeliveryUiState()
    data class Success(
        val order: RiderOrder,
        val currentLocation: LocationUpdate?,
        val distanceToDestination: Double,
        val estimatedTime: Int
    ) : ActiveDeliveryUiState()
    data class Error(val message: String) : ActiveDeliveryUiState()
    object Completed : ActiveDeliveryUiState()
}

@HiltViewModel
class ActiveDeliveryViewModel @Inject constructor(
    private val orderRepository: RiderOrderRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val orderId: String? = savedStateHandle["orderId"]
    
    private val _uiState = MutableStateFlow<ActiveDeliveryUiState>(ActiveDeliveryUiState.Loading)
    val uiState: StateFlow<ActiveDeliveryUiState> = _uiState.asStateFlow()
    
    private val _currentOrder = MutableStateFlow<RiderOrder?>(null)
    val currentOrder: StateFlow<RiderOrder?> = _currentOrder.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<LocationUpdate?>(null)
    val currentLocation: StateFlow<LocationUpdate?> = _currentLocation.asStateFlow()
    
    private val _isUpdatingStatus = MutableStateFlow(false)
    val isUpdatingStatus: StateFlow<Boolean> = _isUpdatingStatus.asStateFlow()
    
    init {
        loadOrderDetails()
        observeLocation()
    }
    
    private fun loadOrderDetails() {
        viewModelScope.launch {
            _uiState.value = ActiveDeliveryUiState.Loading
            
            if (orderId != null) {
                val result = orderRepository.getOrderDetails(orderId)
                result.fold(
                    onSuccess = { order ->
                        _currentOrder.value = order
                        updateUiState(order)
                    },
                    onFailure = { exception ->
                        _uiState.value = ActiveDeliveryUiState.Error(
                            exception.message ?: "Failed to load order"
                        )
                    }
                )
            } else {
                // Try to get active order
                val result = orderRepository.getActiveOrder()
                result.fold(
                    onSuccess = { order ->
                        if (order != null) {
                            _currentOrder.value = order
                            updateUiState(order)
                        } else {
                            _uiState.value = ActiveDeliveryUiState.NoActiveDelivery
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = ActiveDeliveryUiState.Error(
                            exception.message ?: "Failed to load active order"
                        )
                    }
                )
            }
        }
    }
    
    private fun observeLocation() {
        viewModelScope.launch {
            locationRepository.locationUpdates.collect { location ->
                _currentLocation.value = location
                
                // Update location to server
                locationRepository.updateLocationToServer(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    bearing = location.bearing,
                    speed = location.speed
                )
                
                // Update UI state with new distance
                val order = _currentOrder.value
                if (order != null) {
                    updateUiState(order)
                }
            }
        }
    }
    
    private fun updateUiState(order: RiderOrder) {
        val location = _currentLocation.value
        val destination = when (order.status) {
            OrderStatus.ACCEPTED, OrderStatus.ARRIVED_AT_RESTAURANT -> order.restaurantLocation
            else -> order.deliveryLocation
        }
        
        val distance = if (location != null) {
            locationRepository.calculateDistance(
                location.latitude,
                location.longitude,
                destination.latitude,
                destination.longitude
            )
        } else {
            order.distance
        }
        
        val estimatedTime = locationRepository.estimateTime(distance)
        
        _uiState.value = ActiveDeliveryUiState.Success(
            order = order,
            currentLocation = location,
            distanceToDestination = distance,
            estimatedTime = estimatedTime
        )
    }
    
    fun arrivedAtRestaurant() {
        viewModelScope.launch {
            val order = _currentOrder.value ?: return@launch
            _isUpdatingStatus.value = true
            
            val result = orderRepository.updateOrderStatus(order.id, OrderStatus.ARRIVED_AT_RESTAURANT)
            result.fold(
                onSuccess = { updatedOrder ->
                    _currentOrder.value = updatedOrder
                    updateUiState(updatedOrder)
                },
                onFailure = { /* Handle error */ }
            )
            
            _isUpdatingStatus.value = false
        }
    }
    
    fun pickUpOrder() {
        viewModelScope.launch {
            val order = _currentOrder.value ?: return@launch
            _isUpdatingStatus.value = true
            
            val result = orderRepository.markAsPickedUp(order.id)
            result.fold(
                onSuccess = { updatedOrder ->
                    _currentOrder.value = updatedOrder
                    updateUiState(updatedOrder)
                },
                onFailure = { /* Handle error */ }
            )
            
            _isUpdatingStatus.value = false
        }
    }
    
    fun startDelivery() {
        viewModelScope.launch {
            val order = _currentOrder.value ?: return@launch
            _isUpdatingStatus.value = true
            
            val result = orderRepository.updateOrderStatus(order.id, OrderStatus.ON_THE_WAY)
            result.fold(
                onSuccess = { updatedOrder ->
                    _currentOrder.value = updatedOrder
                    updateUiState(updatedOrder)
                },
                onFailure = { /* Handle error */ }
            )
            
            _isUpdatingStatus.value = false
        }
    }
    
    fun completeDelivery(
        proofImageUrl: String? = null,
        signature: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            val order = _currentOrder.value ?: return@launch
            _isUpdatingStatus.value = true
            
            val result = orderRepository.markAsDelivered(
                orderId = order.id,
                proofImageUrl = proofImageUrl,
                signature = signature,
                notes = notes
            )
            
            result.fold(
                onSuccess = {
                    _uiState.value = ActiveDeliveryUiState.Completed
                },
                onFailure = { /* Handle error */ }
            )
            
            _isUpdatingStatus.value = false
        }
    }
    
    fun getNextAction(): Pair<String, () -> Unit>? {
        val order = _currentOrder.value ?: return null
        
        return when (order.status) {
            OrderStatus.ACCEPTED -> "রেস্টুরেন্টে পৌঁছেছি" to ::arrivedAtRestaurant
            OrderStatus.ARRIVED_AT_RESTAURANT -> "অর্ডার নিয়েছি" to ::pickUpOrder
            OrderStatus.PICKED_UP -> "ডেলিভারি শুরু" to ::startDelivery
            OrderStatus.ON_THE_WAY -> "ডেলিভারি সম্পন্ন" to { completeDelivery() }
            else -> null
        }
    }
    
    fun refresh() {
        loadOrderDetails()
    }
}

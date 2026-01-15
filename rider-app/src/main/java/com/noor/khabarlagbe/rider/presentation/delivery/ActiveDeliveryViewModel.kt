package com.noor.khabarlagbe.rider.presentation.delivery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.Location
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.LocationRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveDeliveryViewModel @Inject constructor(
    private val orderRepository: RiderOrderRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DeliveryUiState>(DeliveryUiState.Loading)
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()
    
    private val _order = MutableStateFlow<RiderOrder?>(null)
    val order: StateFlow<RiderOrder?> = _order.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    init {
        loadActiveOrder()
        observeLocation()
    }
    
    private fun loadActiveOrder() {
        viewModelScope.launch {
            try {
                _uiState.value = DeliveryUiState.Loading
                orderRepository.getActiveOrder()
                    .onSuccess { activeOrder ->
                        if (activeOrder != null) {
                            _order.value = activeOrder
                            _uiState.value = DeliveryUiState.Success
                        } else {
                            _uiState.value = DeliveryUiState.NoActiveOrder
                        }
                    }
                    .onFailure { error ->
                        _uiState.value = DeliveryUiState.Error(error.message ?: "ডেলিভারি লোড করতে ব্যর্থ")
                    }
            } catch (e: Exception) {
                _uiState.value = DeliveryUiState.Error(e.message ?: "ডেলিভারি লোড করতে ব্যর্থ")
            }
        }
    }
    
    private fun observeLocation() {
        viewModelScope.launch {
            locationRepository.getLocationUpdates().collect { location ->
                _currentLocation.value = location
            }
        }
    }
    
    fun updateStatus(newStatus: OrderStatus) {
        val currentOrder = _order.value ?: return
        
        viewModelScope.launch {
            orderRepository.updateOrderStatus(currentOrder.id, newStatus)
                .onSuccess { updatedOrder ->
                    _order.value = updatedOrder
                }
                .onFailure {
                    _uiState.value = DeliveryUiState.Error("স্ট্যাটাস আপডেট করতে ব্যর্থ")
                }
        }
    }
    
    fun verifyPickupOtp(otp: String, onSuccess: () -> Unit) {
        val currentOrder = _order.value ?: return
        
        viewModelScope.launch {
            orderRepository.verifyPickupOtp(currentOrder.id, otp)
                .onSuccess { verified ->
                    if (verified) {
                        updateStatus(OrderStatus.PICKED_UP)
                        onSuccess()
                    } else {
                        _uiState.value = DeliveryUiState.Error("ভুল OTP")
                    }
                }
                .onFailure {
                    _uiState.value = DeliveryUiState.Error("OTP যাচাই করতে ব্যর্থ")
                }
        }
    }
    
    fun verifyDeliveryOtp(otp: String, onSuccess: () -> Unit) {
        val currentOrder = _order.value ?: return
        
        viewModelScope.launch {
            orderRepository.verifyDeliveryOtp(currentOrder.id, otp)
                .onSuccess { verified ->
                    if (verified) {
                        completeDelivery()
                        onSuccess()
                    } else {
                        _uiState.value = DeliveryUiState.Error("ভুল OTP")
                    }
                }
                .onFailure {
                    _uiState.value = DeliveryUiState.Error("OTP যাচাই করতে ব্যর্থ")
                }
        }
    }
    
    private fun completeDelivery() {
        val currentOrder = _order.value ?: return
        
        viewModelScope.launch {
            orderRepository.completeDelivery(currentOrder.id)
                .onSuccess {
                    _order.value = null
                    _uiState.value = DeliveryUiState.Completed
                }
                .onFailure {
                    _uiState.value = DeliveryUiState.Error("ডেলিভারি সম্পন্ন করতে ব্যর্থ")
                }
        }
    }
    
    fun reportIssue(issue: String) {
        val currentOrder = _order.value ?: return
        
        viewModelScope.launch {
            orderRepository.reportIssue(currentOrder.id, issue)
                .onSuccess {
                    _uiState.value = DeliveryUiState.IssueReported
                }
                .onFailure {
                    _uiState.value = DeliveryUiState.Error("সমস্যা রিপোর্ট করতে ব্যর্থ")
                }
        }
    }
}

sealed class DeliveryUiState {
    object Loading : DeliveryUiState()
    object Success : DeliveryUiState()
    object NoActiveOrder : DeliveryUiState()
    object Completed : DeliveryUiState()
    object IssueReported : DeliveryUiState()
    data class Error(val message: String) : DeliveryUiState()
}

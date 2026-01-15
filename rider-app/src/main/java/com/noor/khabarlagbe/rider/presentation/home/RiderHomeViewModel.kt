package com.noor.khabarlagbe.rider.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiderHomeViewModel @Inject constructor(
    private val authRepository: RiderAuthRepository,
    private val orderRepository: RiderOrderRepository,
    private val profileRepository: RiderProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _rider = MutableStateFlow<Rider?>(null)
    val rider: StateFlow<Rider?> = _rider.asStateFlow()
    
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val _availableOrders = MutableStateFlow<List<RiderOrder>>(emptyList())
    val availableOrders: StateFlow<List<RiderOrder>> = _availableOrders.asStateFlow()
    
    private val _activeOrder = MutableStateFlow<RiderOrder?>(null)
    val activeOrder: StateFlow<RiderOrder?> = _activeOrder.asStateFlow()
    
    init {
        loadData()
        observeOrders()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading
                
                profileRepository.getRiderProfile()
                    .onSuccess { profile ->
                        _rider.value = profile
                        _isOnline.value = profile.isOnline
                    }
                
                orderRepository.getActiveOrder()
                    .onSuccess { order ->
                        _activeOrder.value = order
                    }
                
                if (_isOnline.value) {
                    orderRepository.getAvailableOrders()
                        .onSuccess { orders ->
                            _availableOrders.value = orders
                        }
                }
                
                _uiState.value = HomeUiState.Success
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "তথ্য লোড করতে ব্যর্থ")
            }
        }
    }
    
    private fun observeOrders() {
        viewModelScope.launch {
            orderRepository.observeAvailableOrders().collect { orders ->
                _availableOrders.value = orders
            }
        }
        
        viewModelScope.launch {
            orderRepository.observeActiveOrder().collect { order ->
                _activeOrder.value = order
            }
        }
    }
    
    fun toggleOnlineStatus() {
        viewModelScope.launch {
            val newStatus = !_isOnline.value
            authRepository.updateOnlineStatus(newStatus)
                .onSuccess {
                    _isOnline.value = newStatus
                    if (newStatus) {
                        refreshAvailableOrders()
                    } else {
                        _availableOrders.value = emptyList()
                    }
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error("স্ট্যাটাস আপডেট করতে ব্যর্থ")
                }
        }
    }
    
    fun refreshAvailableOrders() {
        if (!_isOnline.value) return
        
        viewModelScope.launch {
            orderRepository.getAvailableOrders()
                .onSuccess { orders ->
                    _availableOrders.value = orders
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error("অর্ডার লোড করতে ব্যর্থ")
                }
        }
    }
    
    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.acceptOrder(orderId)
                .onSuccess { order ->
                    _activeOrder.value = order
                    _availableOrders.value = _availableOrders.value.filter { it.id != orderId }
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error("অর্ডার গ্রহণ করতে ব্যর্থ")
                }
        }
    }
    
    fun rejectOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId)
                .onSuccess {
                    _availableOrders.value = _availableOrders.value.filter { it.id != orderId }
                }
                .onFailure {
                    _uiState.value = HomeUiState.Error("অর্ডার বাতিল করতে ব্যর্থ")
                }
        }
    }
    
    fun refresh() {
        loadData()
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

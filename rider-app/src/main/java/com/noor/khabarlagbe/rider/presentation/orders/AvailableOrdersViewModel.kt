package com.noor.khabarlagbe.rider.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailableOrdersViewModel @Inject constructor(
    private val orderRepository: RiderOrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    
    private val _orders = MutableStateFlow<List<RiderOrder>>(emptyList())
    val orders: StateFlow<List<RiderOrder>> = _orders.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private var autoRefreshJob: Job? = null
    
    init {
        loadOrders()
        startAutoRefresh()
    }
    
    private fun loadOrders() {
        viewModelScope.launch {
            try {
                _uiState.value = OrdersUiState.Loading
                orderRepository.getAvailableOrders()
                    .onSuccess { orderList ->
                        _orders.value = orderList
                        _uiState.value = if (orderList.isEmpty()) {
                            OrdersUiState.Empty
                        } else {
                            OrdersUiState.Success
                        }
                    }
                    .onFailure { error ->
                        _uiState.value = OrdersUiState.Error(error.message ?: "অর্ডার লোড করতে ব্যর্থ")
                    }
            } catch (e: Exception) {
                _uiState.value = OrdersUiState.Error(e.message ?: "অর্ডার লোড করতে ব্যর্থ")
            }
        }
    }
    
    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(10000) // Refresh every 10 seconds
                refresh(silent = true)
            }
        }
    }
    
    fun refresh(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _isRefreshing.value = true
            
            orderRepository.getAvailableOrders()
                .onSuccess { orderList ->
                    _orders.value = orderList
                    if (_uiState.value !is OrdersUiState.Success) {
                        _uiState.value = if (orderList.isEmpty()) {
                            OrdersUiState.Empty
                        } else {
                            OrdersUiState.Success
                        }
                    }
                }
                .onFailure {
                    if (!silent) {
                        _uiState.value = OrdersUiState.Error("রিফ্রেশ করতে ব্যর্থ")
                    }
                }
            
            if (!silent) _isRefreshing.value = false
        }
    }
    
    fun acceptOrder(orderId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            orderRepository.acceptOrder(orderId)
                .onSuccess {
                    _orders.value = _orders.value.filter { it.id != orderId }
                    onSuccess()
                }
                .onFailure {
                    _uiState.value = OrdersUiState.Error("অর্ডার গ্রহণ করতে ব্যর্থ")
                }
        }
    }
    
    fun rejectOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId)
                .onSuccess {
                    _orders.value = _orders.value.filter { it.id != orderId }
                }
                .onFailure {
                    _uiState.value = OrdersUiState.Error("অর্ডার বাতিল করতে ব্যর্থ")
                }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
}

sealed class OrdersUiState {
    object Loading : OrdersUiState()
    object Success : OrdersUiState()
    object Empty : OrdersUiState()
    data class Error(val message: String) : OrdersUiState()
}

package com.noor.khabarlagbe.restaurant.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.Order
import com.noor.khabarlagbe.restaurant.domain.model.OrderStatusEnum
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrdersUiState {
    object Loading : OrdersUiState()
    data class Success(
        val newOrders: List<Order>,
        val preparingOrders: List<Order>,
        val readyOrders: List<Order>,
        val completedOrders: List<Order>
    ) : OrdersUiState()
    data class Error(val message: String) : OrdersUiState()
}

sealed class OrderDetailUiState {
    object Loading : OrderDetailUiState()
    data class Success(val order: Order) : OrderDetailUiState()
    data class Error(val message: String) : OrderDetailUiState()
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: RestaurantOrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    
    private val _orderDetailState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val orderDetailState: StateFlow<OrderDetailUiState> = _orderDetailState.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()
    
    private val _showRejectDialog = MutableStateFlow<String?>(null)
    val showRejectDialog: StateFlow<String?> = _showRejectDialog.asStateFlow()
    
    init {
        loadOrders()
        observeOrders()
    }
    
    private fun observeOrders() {
        viewModelScope.launch {
            combine(
                orderRepository.getOrdersByStatus(OrderStatusEnum.PENDING),
                orderRepository.getOrdersByStatus(OrderStatusEnum.PREPARING),
                orderRepository.getOrdersByStatus(OrderStatusEnum.READY),
                orderRepository.getOrdersByStatus(OrderStatusEnum.DELIVERED)
            ) { pending, preparing, ready, delivered ->
                OrdersUiState.Success(
                    newOrders = pending,
                    preparingOrders = preparing,
                    readyOrders = ready,
                    completedOrders = delivered
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = OrdersUiState.Loading
            refresh()
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            orderRepository.refreshOrders()
            _isRefreshing.value = false
        }
    }
    
    fun selectTab(index: Int) {
        _selectedTab.value = index
    }
    
    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _orderDetailState.value = OrderDetailUiState.Loading
            orderRepository.getOrderById(orderId)
                .onSuccess { order ->
                    _orderDetailState.value = OrderDetailUiState.Success(order)
                }
                .onFailure { error ->
                    _orderDetailState.value = OrderDetailUiState.Error(error.message ?: "Failed to load order")
                }
        }
    }
    
    fun observeOrderDetail(orderId: String) {
        viewModelScope.launch {
            orderRepository.observeOrder(orderId).collect { order ->
                if (order != null) {
                    _orderDetailState.value = OrderDetailUiState.Success(order)
                }
            }
        }
    }
    
    fun acceptOrder(orderId: String, prepTime: Int = 20) {
        viewModelScope.launch {
            orderRepository.acceptOrder(orderId, prepTime)
                .onSuccess { refresh() }
        }
    }
    
    fun showRejectDialog(orderId: String) {
        _showRejectDialog.value = orderId
    }
    
    fun dismissRejectDialog() {
        _showRejectDialog.value = null
    }
    
    fun rejectOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId, reason)
                .onSuccess {
                    _showRejectDialog.value = null
                    refresh()
                }
        }
    }
    
    fun markPreparing(orderId: String) {
        viewModelScope.launch {
            orderRepository.markPreparing(orderId)
                .onSuccess { refresh() }
        }
    }
    
    fun markReady(orderId: String) {
        viewModelScope.launch {
            orderRepository.markReady(orderId)
                .onSuccess { refresh() }
        }
    }
}

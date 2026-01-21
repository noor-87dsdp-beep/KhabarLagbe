package com.noor.khabarlagbe.restaurant.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantOrderRepository
import com.noor.khabarlagbe.restaurant.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val restaurant: Restaurant?,
        val stats: RestaurantStats,
        val newOrders: List<Order>,
        val preparingOrders: List<Order>,
        val readyOrders: List<Order>
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

@HiltViewModel
class RestaurantDashboardViewModel @Inject constructor(
    private val authRepository: RestaurantAuthRepository,
    private val orderRepository: RestaurantOrderRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _isOpen = MutableStateFlow(true)
    val isOpen: StateFlow<Boolean> = _isOpen.asStateFlow()
    
    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()
    
    init {
        loadDashboard()
        observeOrders()
    }
    
    private fun observeOrders() {
        viewModelScope.launch {
            combine(
                orderRepository.getOrdersByStatus(OrderStatusEnum.PENDING),
                orderRepository.getOrdersByStatus(OrderStatusEnum.PREPARING),
                orderRepository.getOrdersByStatus(OrderStatusEnum.READY)
            ) { pending, preparing, ready ->
                Triple(pending, preparing, ready)
            }.collect { (pending, preparing, ready) ->
                val currentState = _uiState.value
                if (currentState is DashboardUiState.Success) {
                    _uiState.value = currentState.copy(
                        newOrders = pending,
                        preparingOrders = preparing,
                        readyOrders = ready
                    )
                }
            }
        }
    }
    
    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            
            val statsResult = orderRepository.getRestaurantStats()
            val restaurant = authRepository.getCurrentRestaurant().first()
            
            if (restaurant != null) {
                _isOpen.value = restaurant.isOpen
                _isBusy.value = restaurant.isBusy
            }
            
            statsResult
                .onSuccess { stats ->
                    _uiState.value = DashboardUiState.Success(
                        restaurant = restaurant,
                        stats = stats,
                        newOrders = emptyList(),
                        preparingOrders = emptyList(),
                        readyOrders = emptyList()
                    )
                    refreshOrders()
                }
                .onFailure { error ->
                    _uiState.value = DashboardUiState.Success(
                        restaurant = restaurant,
                        stats = RestaurantStats(0, 0.0, 0.0, 0, 0, 0, 0, 0),
                        newOrders = emptyList(),
                        preparingOrders = emptyList(),
                        readyOrders = emptyList()
                    )
                }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            orderRepository.refreshOrders()
            orderRepository.getRestaurantStats()
                .onSuccess { stats ->
                    val currentState = _uiState.value
                    if (currentState is DashboardUiState.Success) {
                        _uiState.value = currentState.copy(stats = stats)
                    }
                }
            _isRefreshing.value = false
        }
    }
    
    private fun refreshOrders() {
        viewModelScope.launch {
            orderRepository.refreshOrders(OrderStatusEnum.PENDING.toApiString())
            orderRepository.refreshOrders(OrderStatusEnum.PREPARING.toApiString())
            orderRepository.refreshOrders(OrderStatusEnum.READY.toApiString())
        }
    }
    
    fun toggleOpenStatus() {
        viewModelScope.launch {
            val newStatus = !_isOpen.value
            settingsRepository.updateOpenStatus(newStatus)
                .onSuccess { restaurant ->
                    _isOpen.value = restaurant.isOpen
                }
        }
    }
    
    fun toggleBusyMode() {
        viewModelScope.launch {
            val newStatus = !_isBusy.value
            settingsRepository.updateBusyMode(newStatus)
                .onSuccess { restaurant ->
                    _isBusy.value = restaurant.isBusy
                }
        }
    }
    
    fun acceptOrder(orderId: String, prepTime: Int = 20) {
        viewModelScope.launch {
            orderRepository.acceptOrder(orderId, prepTime)
        }
    }
    
    fun rejectOrder(orderId: String, reason: String) {
        viewModelScope.launch {
            orderRepository.rejectOrder(orderId, reason)
        }
    }
    
    fun markPreparing(orderId: String) {
        viewModelScope.launch {
            orderRepository.markPreparing(orderId)
        }
    }
    
    fun markReady(orderId: String) {
        viewModelScope.launch {
            orderRepository.markReady(orderId)
        }
    }
}

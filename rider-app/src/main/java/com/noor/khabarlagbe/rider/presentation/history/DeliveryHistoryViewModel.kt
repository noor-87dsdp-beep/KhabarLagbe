package com.noor.khabarlagbe.rider.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class HistoryFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH
}

sealed class DeliveryHistoryUiState {
    object Loading : DeliveryHistoryUiState()
    object Empty : DeliveryHistoryUiState()
    data class Success(
        val deliveries: List<RiderOrder>,
        val filter: HistoryFilter,
        val totalCount: Int
    ) : DeliveryHistoryUiState()
    data class Error(val message: String) : DeliveryHistoryUiState()
}

@HiltViewModel
class DeliveryHistoryViewModel @Inject constructor(
    private val orderRepository: RiderOrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DeliveryHistoryUiState>(DeliveryHistoryUiState.Loading)
    val uiState: StateFlow<DeliveryHistoryUiState> = _uiState.asStateFlow()
    
    private val _deliveries = MutableStateFlow<List<RiderOrder>>(emptyList())
    val deliveries: StateFlow<List<RiderOrder>> = _deliveries.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow(HistoryFilter.ALL)
    val selectedFilter: StateFlow<HistoryFilter> = _selectedFilter.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private var currentPage = 1
    private var hasMorePages = true
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    init {
        loadDeliveryHistory()
        observeLocalHistory()
    }
    
    private fun observeLocalHistory() {
        viewModelScope.launch {
            orderRepository.getLocalDeliveryHistory().collect { localDeliveries ->
                if (_deliveries.value.isEmpty()) {
                    _deliveries.value = localDeliveries
                    updateUiState()
                }
            }
        }
    }
    
    fun loadDeliveryHistory(reset: Boolean = false) {
        if (reset) {
            currentPage = 1
            hasMorePages = true
            _deliveries.value = emptyList()
        }
        
        if (!hasMorePages) return
        
        viewModelScope.launch {
            if (currentPage == 1) {
                _uiState.value = DeliveryHistoryUiState.Loading
            }
            
            val (startDate, endDate) = getDateRange(_selectedFilter.value)
            
            val result = orderRepository.getDeliveryHistory(
                page = currentPage,
                startDate = startDate,
                endDate = endDate
            )
            
            result.fold(
                onSuccess = { newDeliveries ->
                    if (currentPage == 1) {
                        _deliveries.value = newDeliveries
                    } else {
                        _deliveries.value = _deliveries.value + newDeliveries
                    }
                    hasMorePages = newDeliveries.size >= 20
                    currentPage++
                    updateUiState()
                },
                onFailure = { exception ->
                    if (_deliveries.value.isEmpty()) {
                        _uiState.value = DeliveryHistoryUiState.Error(
                            exception.message ?: "Failed to load delivery history"
                        )
                    }
                }
            )
        }
    }
    
    fun loadMoreIfNeeded(lastVisibleIndex: Int) {
        if (lastVisibleIndex >= _deliveries.value.size - 5 && hasMorePages) {
            loadDeliveryHistory()
        }
    }
    
    fun setFilter(filter: HistoryFilter) {
        _selectedFilter.value = filter
        loadDeliveryHistory(reset = true)
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadDeliveryHistory(reset = true)
        } else {
            searchDeliveries(query)
        }
    }
    
    private fun searchDeliveries(query: String) {
        viewModelScope.launch {
            _uiState.value = DeliveryHistoryUiState.Loading
            
            val results = orderRepository.searchDeliveryHistory(query)
            _deliveries.value = results
            updateUiState()
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadDeliveryHistory(reset = true)
            _isRefreshing.value = false
        }
    }
    
    private fun updateUiState() {
        val deliveries = _deliveries.value
        _uiState.value = if (deliveries.isEmpty()) {
            DeliveryHistoryUiState.Empty
        } else {
            DeliveryHistoryUiState.Success(
                deliveries = deliveries,
                filter = _selectedFilter.value,
                totalCount = deliveries.size
            )
        }
    }
    
    private fun getDateRange(filter: HistoryFilter): Pair<String?, String?> {
        if (filter == HistoryFilter.ALL) return Pair(null, null)
        
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        
        when (filter) {
            HistoryFilter.TODAY -> { }
            HistoryFilter.THIS_WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            HistoryFilter.THIS_MONTH -> calendar.add(Calendar.DAY_OF_YEAR, -30)
            HistoryFilter.ALL -> { }
        }
        
        val startDate = dateFormat.format(calendar.time)
        return Pair(startDate, endDate)
    }
    
    fun getDeliveryStats(): DeliveryStats {
        val deliveries = _deliveries.value
        val completedDeliveries = deliveries.filter { 
            it.status == com.noor.khabarlagbe.rider.domain.model.OrderStatus.DELIVERED 
        }
        
        return DeliveryStats(
            totalDeliveries = completedDeliveries.size,
            totalEarnings = completedDeliveries.sumOf { it.deliveryFee },
            totalDistance = completedDeliveries.sumOf { it.distance },
            averageDeliveryTime = if (completedDeliveries.isNotEmpty()) {
                completedDeliveries.sumOf { it.estimatedTime } / completedDeliveries.size
            } else 0
        )
    }
}

data class DeliveryStats(
    val totalDeliveries: Int,
    val totalEarnings: Double,
    val totalDistance: Double,
    val averageDeliveryTime: Int
)

package com.noor.khabarlagbe.rider.presentation.earnings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.data.dto.DailyEarningsDto
import com.noor.khabarlagbe.rider.data.dto.WithdrawalDto
import com.noor.khabarlagbe.rider.domain.model.Earnings
import com.noor.khabarlagbe.rider.domain.model.EarningsEntry
import com.noor.khabarlagbe.rider.domain.model.EarningsSummary
import com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class EarningsPeriod {
    TODAY, THIS_WEEK, THIS_MONTH, CUSTOM
}

sealed class EarningsUiState {
    object Loading : EarningsUiState()
    data class Success(
        val earnings: Earnings,
        val selectedPeriod: EarningsPeriod,
        val dailyBreakdown: List<DailyEarningsDto>,
        val availableBalance: Double,
        val withdrawalHistory: List<WithdrawalDto>
    ) : EarningsUiState()
    data class Error(val message: String) : EarningsUiState()
}

sealed class WithdrawalUiState {
    object Idle : WithdrawalUiState()
    object Loading : WithdrawalUiState()
    data class Success(val withdrawal: WithdrawalDto) : WithdrawalUiState()
    data class Error(val message: String) : WithdrawalUiState()
}

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val earningsRepository: RiderEarningsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<EarningsUiState>(EarningsUiState.Loading)
    val uiState: StateFlow<EarningsUiState> = _uiState.asStateFlow()
    
    private val _withdrawalState = MutableStateFlow<WithdrawalUiState>(WithdrawalUiState.Idle)
    val withdrawalState: StateFlow<WithdrawalUiState> = _withdrawalState.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow(EarningsPeriod.TODAY)
    val selectedPeriod: StateFlow<EarningsPeriod> = _selectedPeriod.asStateFlow()
    
    private val _earnings = MutableStateFlow<Earnings?>(null)
    val earnings: StateFlow<Earnings?> = _earnings.asStateFlow()
    
    private val _dailyBreakdown = MutableStateFlow<List<DailyEarningsDto>>(emptyList())
    val dailyBreakdown: StateFlow<List<DailyEarningsDto>> = _dailyBreakdown.asStateFlow()
    
    private val _availableBalance = MutableStateFlow(0.0)
    val availableBalance: StateFlow<Double> = _availableBalance.asStateFlow()
    
    private val _earningsHistory = MutableStateFlow<List<EarningsEntry>>(emptyList())
    val earningsHistory: StateFlow<List<EarningsEntry>> = _earningsHistory.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    init {
        loadEarnings()
    }
    
    fun loadEarnings() {
        viewModelScope.launch {
            _uiState.value = EarningsUiState.Loading
            
            val result = earningsRepository.getEarnings(_selectedPeriod.value.name.lowercase())
            result.fold(
                onSuccess = { earnings ->
                    _earnings.value = earnings
                    
                    // Load additional data
                    val (startDate, endDate) = getDateRange(_selectedPeriod.value)
                    val summaryResult = earningsRepository.getEarningsSummary(startDate, endDate)
                    summaryResult.fold(
                        onSuccess = { (_, breakdown) ->
                            _dailyBreakdown.value = breakdown
                        },
                        onFailure = { }
                    )
                    
                    val balanceResult = earningsRepository.getAvailableBalance()
                    balanceResult.fold(
                        onSuccess = { balance ->
                            _availableBalance.value = balance
                        },
                        onFailure = { }
                    )
                    
                    val withdrawalResult = earningsRepository.getWithdrawalHistory()
                    
                    _uiState.value = EarningsUiState.Success(
                        earnings = earnings,
                        selectedPeriod = _selectedPeriod.value,
                        dailyBreakdown = _dailyBreakdown.value,
                        availableBalance = _availableBalance.value,
                        withdrawalHistory = withdrawalResult.getOrDefault(emptyList())
                    )
                },
                onFailure = { exception ->
                    _uiState.value = EarningsUiState.Error(
                        exception.message ?: "Failed to load earnings"
                    )
                }
            )
        }
    }
    
    fun selectPeriod(period: EarningsPeriod) {
        _selectedPeriod.value = period
        loadEarnings()
    }
    
    fun loadEarningsHistory(page: Int = 1) {
        viewModelScope.launch {
            val (startDate, endDate) = getDateRange(_selectedPeriod.value)
            val result = earningsRepository.getEarningsHistory(
                page = page,
                startDate = startDate,
                endDate = endDate
            )
            result.fold(
                onSuccess = { entries ->
                    _earningsHistory.value = if (page == 1) entries else _earningsHistory.value + entries
                },
                onFailure = { }
            )
        }
    }
    
    fun requestWithdrawal(amount: Double, method: String, accountDetails: String?) {
        viewModelScope.launch {
            _withdrawalState.value = WithdrawalUiState.Loading
            
            val result = earningsRepository.requestWithdrawal(amount, method, accountDetails)
            result.fold(
                onSuccess = { withdrawal ->
                    _withdrawalState.value = WithdrawalUiState.Success(withdrawal)
                    // Refresh balance
                    val balanceResult = earningsRepository.getAvailableBalance()
                    balanceResult.fold(
                        onSuccess = { balance ->
                            _availableBalance.value = balance
                        },
                        onFailure = { }
                    )
                },
                onFailure = { exception ->
                    _withdrawalState.value = WithdrawalUiState.Error(
                        exception.message ?: "Failed to request withdrawal"
                    )
                }
            )
        }
    }
    
    fun resetWithdrawalState() {
        _withdrawalState.value = WithdrawalUiState.Idle
    }
    
    private fun getDateRange(period: EarningsPeriod): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        
        when (period) {
            EarningsPeriod.TODAY -> { }
            EarningsPeriod.THIS_WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            EarningsPeriod.THIS_MONTH -> calendar.add(Calendar.DAY_OF_YEAR, -30)
            EarningsPeriod.CUSTOM -> calendar.add(Calendar.DAY_OF_YEAR, -30)
        }
        
        val startDate = dateFormat.format(calendar.time)
        return Pair(startDate, endDate)
    }
    
    fun getSelectedSummary(): EarningsSummary? {
        val earnings = _earnings.value ?: return null
        return when (_selectedPeriod.value) {
            EarningsPeriod.TODAY -> earnings.today
            EarningsPeriod.THIS_WEEK -> earnings.thisWeek
            EarningsPeriod.THIS_MONTH -> earnings.thisMonth
            EarningsPeriod.CUSTOM -> earnings.thisMonth
        }
    }
}

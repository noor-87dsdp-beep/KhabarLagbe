package com.noor.khabarlagbe.rider.presentation.earnings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.Earnings
import com.noor.khabarlagbe.rider.domain.model.EarningsEntry
import com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val earningsRepository: RiderEarningsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<EarningsUiState>(EarningsUiState.Loading)
    val uiState: StateFlow<EarningsUiState> = _uiState.asStateFlow()
    
    private val _earnings = MutableStateFlow<Earnings?>(null)
    val earnings: StateFlow<Earnings?> = _earnings.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow(EarningsPeriod.TODAY)
    val selectedPeriod: StateFlow<EarningsPeriod> = _selectedPeriod.asStateFlow()
    
    private val _transactionHistory = MutableStateFlow<List<EarningsEntry>>(emptyList())
    val transactionHistory: StateFlow<List<EarningsEntry>> = _transactionHistory.asStateFlow()
    
    init {
        loadEarnings()
    }
    
    private fun loadEarnings() {
        viewModelScope.launch {
            try {
                _uiState.value = EarningsUiState.Loading
                earningsRepository.getTodayEarnings()
                    .onSuccess { earningsData ->
                        _earnings.value = earningsData
                        _uiState.value = EarningsUiState.Success
                    }
                    .onFailure { error ->
                        _uiState.value = EarningsUiState.Error(error.message ?: "আয় তথ্য লোড করতে ব্যর্থ")
                    }
            } catch (e: Exception) {
                _uiState.value = EarningsUiState.Error(e.message ?: "আয় তথ্য লোড করতে ব্যর্থ")
            }
        }
    }
    
    fun selectPeriod(period: EarningsPeriod) {
        _selectedPeriod.value = period
        loadTransactionHistory()
    }
    
    private fun loadTransactionHistory() {
        viewModelScope.launch {
            earningsRepository.getTransactionHistory(1, 50)
                .onSuccess { history ->
                    _transactionHistory.value = history
                }
        }
    }
    
    fun refresh() {
        loadEarnings()
        loadTransactionHistory()
    }
}

enum class EarningsPeriod {
    TODAY, THIS_WEEK, THIS_MONTH, CUSTOM
}

sealed class EarningsUiState {
    object Loading : EarningsUiState()
    object Success : EarningsUiState()
    data class Error(val message: String) : EarningsUiState()
}

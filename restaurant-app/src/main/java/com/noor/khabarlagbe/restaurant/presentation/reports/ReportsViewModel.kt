package com.noor.khabarlagbe.restaurant.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.Reports
import com.noor.khabarlagbe.restaurant.domain.repository.ReportsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

sealed class ReportsUiState {
    object Loading : ReportsUiState()
    data class Success(val reports: Reports) : ReportsUiState()
    data class Error(val message: String) : ReportsUiState()
}

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val downloadUrl: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

enum class DateRangeOption {
    TODAY,
    YESTERDAY,
    LAST_7_DAYS,
    LAST_30_DAYS,
    THIS_MONTH,
    LAST_MONTH,
    CUSTOM
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ReportsUiState>(ReportsUiState.Loading)
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()
    
    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()
    
    private val _selectedDateRange = MutableStateFlow(DateRangeOption.LAST_7_DAYS)
    val selectedDateRange: StateFlow<DateRangeOption> = _selectedDateRange.asStateFlow()
    
    private val _startDate = MutableStateFlow(getDateString(-7))
    val startDate: StateFlow<String> = _startDate.asStateFlow()
    
    private val _endDate = MutableStateFlow(getDateString(0))
    val endDate: StateFlow<String> = _endDate.asStateFlow()
    
    private val _period = MutableStateFlow("daily")
    val period: StateFlow<String> = _period.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    init {
        loadReports()
    }
    
    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = ReportsUiState.Loading
            reportsRepository.getReports(_startDate.value, _endDate.value, _period.value)
                .onSuccess { reports ->
                    _uiState.value = ReportsUiState.Success(reports)
                }
                .onFailure { error ->
                    _uiState.value = ReportsUiState.Error(error.message ?: "Failed to load reports")
                }
        }
    }
    
    fun selectDateRange(option: DateRangeOption) {
        _selectedDateRange.value = option
        when (option) {
            DateRangeOption.TODAY -> {
                _startDate.value = getDateString(0)
                _endDate.value = getDateString(0)
            }
            DateRangeOption.YESTERDAY -> {
                _startDate.value = getDateString(-1)
                _endDate.value = getDateString(-1)
            }
            DateRangeOption.LAST_7_DAYS -> {
                _startDate.value = getDateString(-7)
                _endDate.value = getDateString(0)
            }
            DateRangeOption.LAST_30_DAYS -> {
                _startDate.value = getDateString(-30)
                _endDate.value = getDateString(0)
            }
            DateRangeOption.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                _startDate.value = dateFormat.format(cal.time)
                _endDate.value = getDateString(0)
            }
            DateRangeOption.LAST_MONTH -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, -1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                _startDate.value = dateFormat.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                _endDate.value = dateFormat.format(cal.time)
            }
            DateRangeOption.CUSTOM -> {}
        }
        if (option != DateRangeOption.CUSTOM) {
            loadReports()
        }
    }
    
    fun setCustomDateRange(start: String, end: String) {
        _selectedDateRange.value = DateRangeOption.CUSTOM
        _startDate.value = start
        _endDate.value = end
        loadReports()
    }
    
    fun setPeriod(newPeriod: String) {
        _period.value = newPeriod
        loadReports()
    }
    
    fun exportReport(format: String = "pdf") {
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            reportsRepository.exportReport(_startDate.value, _endDate.value, format)
                .onSuccess { url ->
                    _exportState.value = ExportState.Success(url)
                }
                .onFailure { error ->
                    _exportState.value = ExportState.Error(error.message ?: "Export failed")
                }
        }
    }
    
    fun clearExportState() {
        _exportState.value = ExportState.Idle
    }
    
    private fun getDateString(daysOffset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysOffset)
        return dateFormat.format(cal.time)
    }
}

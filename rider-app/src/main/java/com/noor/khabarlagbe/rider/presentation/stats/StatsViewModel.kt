package com.noor.khabarlagbe.rider.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.data.api.RiderApi
import com.noor.khabarlagbe.rider.data.dto.LeaderboardEntryDto
import com.noor.khabarlagbe.rider.data.dto.StatsDto
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StatsUiState {
    object Loading : StatsUiState()
    data class Success(
        val stats: StatsDto,
        val leaderboard: List<LeaderboardEntryDto>,
        val myPosition: Int,
        val selectedPeriod: String
    ) : StatsUiState()
    data class Error(val message: String) : StatsUiState()
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val riderApi: RiderApi,
    private val authRepository: RiderAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow("week")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()
    
    init {
        loadStats()
    }
    
    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading
            
            try {
                val token = authRepository.getAccessToken()
                if (token != null) {
                    val statsResponse = riderApi.getStats("Bearer $token", _selectedPeriod.value)
                    val leaderboardResponse = riderApi.getLeaderboard("Bearer $token", _selectedPeriod.value)
                    
                    if (statsResponse.isSuccessful && statsResponse.body() != null) {
                        val stats = statsResponse.body()!!
                        val leaderboard = leaderboardResponse.body()?.leaderboard ?: emptyList()
                        val myPosition = leaderboardResponse.body()?.myPosition ?: 0
                        
                        _uiState.value = StatsUiState.Success(
                            stats = stats,
                            leaderboard = leaderboard,
                            myPosition = myPosition,
                            selectedPeriod = _selectedPeriod.value
                        )
                    } else {
                        _uiState.value = StatsUiState.Error("Failed to load stats")
                    }
                } else {
                    _uiState.value = StatsUiState.Error("Not logged in")
                }
            } catch (e: Exception) {
                _uiState.value = StatsUiState.Error(e.message ?: "Failed to load stats")
            }
        }
    }
    
    fun selectPeriod(period: String) {
        _selectedPeriod.value = period
        loadStats()
    }
}

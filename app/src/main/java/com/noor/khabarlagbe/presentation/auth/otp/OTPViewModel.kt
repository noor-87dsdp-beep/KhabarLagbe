package com.noor.khabarlagbe.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for OTP Verification screen
 * Handles OTP verification and resend logic with countdown timer
 */
@HiltViewModel
class OTPViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OTPUiState>(OTPUiState.Idle)
    val uiState: StateFlow<OTPUiState> = _uiState.asStateFlow()

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    private var countdownJob: Job? = null
    private var phoneNumber: String = ""

    companion object {
        private const val COUNTDOWN_SECONDS = 60
    }

    /**
     * Set phone number for OTP verification
     */
    fun setPhoneNumber(phone: String) {
        phoneNumber = phone
    }

    /**
     * Verify OTP code
     */
    fun verifyOtp(otp: String) {
        if (otp.isBlank() || otp.length != 6) {
            _uiState.value = OTPUiState.Error("Please enter a valid 6-digit OTP")
            return
        }

        if (phoneNumber.isBlank()) {
            _uiState.value = OTPUiState.Error("Phone number is missing")
            return
        }

        viewModelScope.launch {
            _uiState.value = OTPUiState.Loading
            authRepository.verifyOtp(phoneNumber, otp)
                .onSuccess { isValid ->
                    if (isValid) {
                        _uiState.value = OTPUiState.Success
                    } else {
                        _uiState.value = OTPUiState.Error("Invalid OTP. Please try again.")
                    }
                }
                .onFailure { error ->
                    _uiState.value = OTPUiState.Error(
                        error.message ?: "OTP verification failed. Please try again."
                    )
                }
        }
    }

    /**
     * Resend OTP to phone number
     */
    fun resendOtp() {
        if (phoneNumber.isBlank()) {
            _uiState.value = OTPUiState.Error("Phone number is missing")
            return
        }

        if (_countdown.value > 0) {
            return
        }

        viewModelScope.launch {
            _uiState.value = OTPUiState.Loading
            authRepository.sendOtp(phoneNumber)
                .onSuccess {
                    _uiState.value = OTPUiState.OtpResent
                    startCountdown()
                }
                .onFailure { error ->
                    _uiState.value = OTPUiState.Error(
                        error.message ?: "Failed to resend OTP. Please try again."
                    )
                }
        }
    }

    /**
     * Start countdown timer for resend OTP
     */
    fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _countdown.value = COUNTDOWN_SECONDS
            while (_countdown.value > 0) {
                delay(1000)
                _countdown.value = _countdown.value - 1
            }
        }
    }

    /**
     * Reset state to idle
     */
    fun resetState() {
        _uiState.value = OTPUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

/**
 * UI state for OTP Verification screen
 */
sealed class OTPUiState {
    object Idle : OTPUiState()
    object Loading : OTPUiState()
    object Success : OTPUiState()
    object OtpResent : OTPUiState()
    data class Error(val message: String) : OTPUiState()
}

package com.noor.khabarlagbe.rider.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiderAuthViewModel @Inject constructor(
    private val authRepository: RiderAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    fun login(phone: String, password: String) {
        if (phone.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("ফোন নম্বর এবং পাসওয়ার্ড দিন")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(phone, password)
                .onSuccess { rider ->
                    _uiState.value = AuthUiState.Success(rider)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "লগইন ব্যর্থ হয়েছে")
                }
        }
    }
    
    fun register(
        name: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String,
        vehicleType: String,
        vehicleMake: String,
        vehicleModel: String,
        plateNumber: String,
        nidNumber: String,
        licenseNumber: String
    ) {
        when {
            name.isBlank() -> {
                _uiState.value = AuthUiState.Error("নাম দিন")
                return
            }
            phone.isBlank() || phone.length != 11 -> {
                _uiState.value = AuthUiState.Error("সঠিক ফোন নম্বর দিন")
                return
            }
            password.isBlank() || password.length < 6 -> {
                _uiState.value = AuthUiState.Error("পাসওয়ার্ড অন্তত ৬ অক্ষরের হতে হবে")
                return
            }
            password != confirmPassword -> {
                _uiState.value = AuthUiState.Error("পাসওয়ার্ড মিলছে না")
                return
            }
            vehicleType.isBlank() -> {
                _uiState.value = AuthUiState.Error("গাড়ির ধরন নির্বাচন করুন")
                return
            }
            plateNumber.isBlank() -> {
                _uiState.value = AuthUiState.Error("গাড়ির নম্বর দিন")
                return
            }
            nidNumber.isBlank() -> {
                _uiState.value = AuthUiState.Error("এনআইডি নম্বর দিন")
                return
            }
            licenseNumber.isBlank() -> {
                _uiState.value = AuthUiState.Error("লাইসেন্স নম্বর দিন")
                return
            }
        }
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(
                name, phone, email, password, vehicleType,
                vehicleMake, vehicleModel, plateNumber, nidNumber, licenseNumber
            )
                .onSuccess { rider ->
                    _uiState.value = AuthUiState.Success(rider)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "নিবন্ধন ব্যর্থ হয়েছে")
                }
        }
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val rider: Rider) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

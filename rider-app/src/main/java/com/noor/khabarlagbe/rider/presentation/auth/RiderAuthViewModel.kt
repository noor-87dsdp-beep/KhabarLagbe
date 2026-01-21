package com.noor.khabarlagbe.rider.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.data.repository.OtpRequiredException
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class RequiresOtp(val phone: String) : AuthUiState()
    data class Success(val rider: Rider) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class RegistrationStep {
    object PersonalInfo : RegistrationStep()
    object VehicleInfo : RegistrationStep()
    object Documents : RegistrationStep()
    object BankDetails : RegistrationStep()
}

data class RegistrationData(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val vehicleType: String = "MOTORCYCLE",
    val vehicleNumber: String = "",
    val licenseNumber: String = "",
    val nidNumber: String = "",
    val nidFrontUrl: String = "",
    val nidBackUrl: String = "",
    val licenseUrl: String = "",
    val profilePhotoUrl: String = "",
    val bankAccountName: String = "",
    val bankAccountNumber: String = "",
    val bankName: String = "",
    val bikashNumber: String = "",
    val nagadNumber: String = ""
)

@HiltViewModel
class RiderAuthViewModel @Inject constructor(
    private val authRepository: RiderAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _registrationStep = MutableStateFlow<RegistrationStep>(RegistrationStep.PersonalInfo)
    val registrationStep: StateFlow<RegistrationStep> = _registrationStep.asStateFlow()
    
    private val _registrationData = MutableStateFlow(RegistrationData())
    val registrationData: StateFlow<RegistrationData> = _registrationData.asStateFlow()
    
    val isLoggedIn = authRepository.isLoggedIn
    val currentRider = authRepository.currentRider
    
    private var pendingPhone: String = ""
    
    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            pendingPhone = phone
            
            val result = authRepository.login(phone, password)
            result.fold(
                onSuccess = { rider ->
                    _uiState.value = AuthUiState.Success(rider)
                },
                onFailure = { exception ->
                    if (exception is OtpRequiredException) {
                        _uiState.value = AuthUiState.RequiresOtp(phone)
                    } else {
                        _uiState.value = AuthUiState.Error(exception.message ?: "Login failed")
                    }
                }
            )
        }
    }
    
    fun updateRegistrationData(data: RegistrationData) {
        _registrationData.value = data
    }
    
    fun nextRegistrationStep() {
        _registrationStep.value = when (_registrationStep.value) {
            RegistrationStep.PersonalInfo -> RegistrationStep.VehicleInfo
            RegistrationStep.VehicleInfo -> RegistrationStep.Documents
            RegistrationStep.Documents -> RegistrationStep.BankDetails
            RegistrationStep.BankDetails -> RegistrationStep.BankDetails
        }
    }
    
    fun previousRegistrationStep() {
        _registrationStep.value = when (_registrationStep.value) {
            RegistrationStep.PersonalInfo -> RegistrationStep.PersonalInfo
            RegistrationStep.VehicleInfo -> RegistrationStep.PersonalInfo
            RegistrationStep.Documents -> RegistrationStep.VehicleInfo
            RegistrationStep.BankDetails -> RegistrationStep.Documents
        }
    }
    
    fun register() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val data = _registrationData.value
            pendingPhone = data.phone
            
            val result = authRepository.register(
                name = data.name,
                phone = data.phone,
                email = data.email.ifBlank { null },
                password = data.password,
                vehicleType = data.vehicleType,
                vehicleNumber = data.vehicleNumber,
                licenseNumber = data.licenseNumber,
                nidNumber = data.nidNumber,
                bankAccountName = data.bankAccountName.ifBlank { null },
                bankAccountNumber = data.bankAccountNumber.ifBlank { null },
                bankName = data.bankName.ifBlank { null },
                bikashNumber = data.bikashNumber.ifBlank { null },
                nagadNumber = data.nagadNumber.ifBlank { null }
            )
            
            result.fold(
                onSuccess = { rider ->
                    _uiState.value = AuthUiState.Success(rider)
                },
                onFailure = { exception ->
                    if (exception is OtpRequiredException) {
                        _uiState.value = AuthUiState.RequiresOtp(data.phone)
                    } else {
                        _uiState.value = AuthUiState.Error(exception.message ?: "Registration failed")
                    }
                }
            )
        }
    }
    
    fun verifyOtp(otp: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            val result = authRepository.verifyOtp(pendingPhone, otp)
            result.fold(
                onSuccess = { rider ->
                    _uiState.value = AuthUiState.Success(rider)
                },
                onFailure = { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "OTP verification failed")
                }
            )
        }
    }
    
    fun resendOtp() {
        viewModelScope.launch {
            authRepository.resendOtp(pendingPhone)
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState.Idle
        }
    }
    
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
    
    fun resetRegistration() {
        _registrationStep.value = RegistrationStep.PersonalInfo
        _registrationData.value = RegistrationData()
    }
}

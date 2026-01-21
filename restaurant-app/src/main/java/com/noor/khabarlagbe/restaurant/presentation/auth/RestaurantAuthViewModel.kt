package com.noor.khabarlagbe.restaurant.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val restaurant: Restaurant) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

sealed class RegistrationStep {
    object BusinessInfo : RegistrationStep()
    object Location : RegistrationStep()
    object Documents : RegistrationStep()
    object BankDetails : RegistrationStep()
}

data class RegistrationFormState(
    val businessName: String = "",
    val ownerName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val street: String = "",
    val city: String = "",
    val area: String = "",
    val postalCode: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cuisineTypes: List<String> = emptyList(),
    val tradeLicense: String = "",
    val nidFront: String = "",
    val nidBack: String = "",
    val restaurantPhoto: String = "",
    val bankName: String = "",
    val accountName: String = "",
    val accountNumber: String = "",
    val branchName: String = "",
    val routingNumber: String = ""
)

@HiltViewModel
class RestaurantAuthViewModel @Inject constructor(
    private val authRepository: RestaurantAuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()
    
    private val _registrationState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val registrationState: StateFlow<AuthUiState> = _registrationState.asStateFlow()
    
    private val _currentStep = MutableStateFlow<RegistrationStep>(RegistrationStep.BusinessInfo)
    val currentStep: StateFlow<RegistrationStep> = _currentStep.asStateFlow()
    
    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState: StateFlow<RegistrationFormState> = _formState.asStateFlow()
    
    private val _forgotPasswordState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val forgotPasswordState: StateFlow<AuthUiState> = _forgotPasswordState.asStateFlow()
    
    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading
            authRepository.login(email, password)
                .onSuccess { result ->
                    _loginState.value = AuthUiState.Success(result.restaurant)
                }
                .onFailure { error ->
                    _loginState.value = AuthUiState.Error(error.message ?: "Login failed")
                }
        }
    }
    
    fun register() {
        viewModelScope.launch {
            _registrationState.value = AuthUiState.Loading
            val form = _formState.value
            val data = RegistrationData(
                businessName = form.businessName,
                ownerName = form.ownerName,
                email = form.email,
                phone = form.phone,
                password = form.password,
                address = Address(
                    street = form.street,
                    city = form.city,
                    area = form.area,
                    postalCode = form.postalCode,
                    latitude = form.latitude,
                    longitude = form.longitude
                ),
                cuisineTypes = form.cuisineTypes,
                documents = Documents(
                    tradeLicense = form.tradeLicense,
                    nidFront = form.nidFront,
                    nidBack = form.nidBack,
                    restaurantPhoto = form.restaurantPhoto
                ),
                bankDetails = BankDetails(
                    bankName = form.bankName,
                    accountName = form.accountName,
                    accountNumber = form.accountNumber,
                    branchName = form.branchName,
                    routingNumber = form.routingNumber.ifEmpty { null }
                )
            )
            authRepository.register(data)
                .onSuccess { result ->
                    _registrationState.value = AuthUiState.Success(result.restaurant)
                }
                .onFailure { error ->
                    _registrationState.value = AuthUiState.Error(error.message ?: "Registration failed")
                }
        }
    }
    
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = AuthUiState.Loading
            authRepository.forgotPassword(email)
                .onSuccess {
                    _forgotPasswordState.value = AuthUiState.Initial
                }
                .onFailure { error ->
                    _forgotPasswordState.value = AuthUiState.Error(error.message ?: "Failed to send reset email")
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = AuthUiState.Initial
        }
    }
    
    fun updateFormState(update: RegistrationFormState.() -> RegistrationFormState) {
        _formState.value = _formState.value.update()
    }
    
    fun nextStep() {
        _currentStep.value = when (_currentStep.value) {
            RegistrationStep.BusinessInfo -> RegistrationStep.Location
            RegistrationStep.Location -> RegistrationStep.Documents
            RegistrationStep.Documents -> RegistrationStep.BankDetails
            RegistrationStep.BankDetails -> RegistrationStep.BankDetails
        }
    }
    
    fun previousStep() {
        _currentStep.value = when (_currentStep.value) {
            RegistrationStep.BusinessInfo -> RegistrationStep.BusinessInfo
            RegistrationStep.Location -> RegistrationStep.BusinessInfo
            RegistrationStep.Documents -> RegistrationStep.Location
            RegistrationStep.BankDetails -> RegistrationStep.Documents
        }
    }
    
    fun resetStates() {
        _loginState.value = AuthUiState.Initial
        _registrationState.value = AuthUiState.Initial
        _forgotPasswordState.value = AuthUiState.Initial
    }
    
    fun clearForm() {
        _formState.value = RegistrationFormState()
        _currentStep.value = RegistrationStep.BusinessInfo
    }
}

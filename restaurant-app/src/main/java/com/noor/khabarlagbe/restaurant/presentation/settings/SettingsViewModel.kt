package com.noor.khabarlagbe.restaurant.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import com.noor.khabarlagbe.restaurant.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(val restaurant: Restaurant) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}

data class SettingsFormState(
    val businessName: String = "",
    val phone: String = "",
    val street: String = "",
    val city: String = "",
    val area: String = "",
    val postalCode: String = "",
    val minimumOrder: String = "",
    val deliveryRadius: String = "",
    val estimatedPrepTime: String = "",
    val packagingCharge: String = "",
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: RestaurantAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()
    
    private val _formState = MutableStateFlow(SettingsFormState())
    val formState: StateFlow<SettingsFormState> = _formState.asStateFlow()
    
    private val _isOpen = MutableStateFlow(true)
    val isOpen: StateFlow<Boolean> = _isOpen.asStateFlow()
    
    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()
    
    private val _operatingHours = MutableStateFlow<OperatingHours?>(null)
    val operatingHours: StateFlow<OperatingHours?> = _operatingHours.asStateFlow()
    
    private var currentRestaurant: Restaurant? = null
    
    init {
        loadSettings()
    }
    
    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            settingsRepository.getRestaurantProfile()
                .onSuccess { restaurant ->
                    currentRestaurant = restaurant
                    _isOpen.value = restaurant.isOpen
                    _isBusy.value = restaurant.isBusy
                    _operatingHours.value = restaurant.operatingHours
                    _formState.value = SettingsFormState(
                        businessName = restaurant.businessName,
                        phone = restaurant.phone,
                        street = restaurant.address.street,
                        city = restaurant.address.city,
                        area = restaurant.address.area,
                        postalCode = restaurant.address.postalCode,
                        minimumOrder = restaurant.deliverySettings?.minimumOrder?.toString() ?: "",
                        deliveryRadius = restaurant.deliverySettings?.deliveryRadius?.toString() ?: "",
                        estimatedPrepTime = restaurant.deliverySettings?.estimatedPrepTime?.toString() ?: "",
                        packagingCharge = restaurant.deliverySettings?.packagingCharge?.toString() ?: ""
                    )
                    _uiState.value = SettingsUiState.Success(restaurant)
                }
                .onFailure { error ->
                    _uiState.value = SettingsUiState.Error(error.message ?: "Failed to load settings")
                }
        }
    }
    
    fun updateFormState(update: SettingsFormState.() -> SettingsFormState) {
        _formState.value = _formState.value.update()
    }
    
    fun toggleOpenStatus() {
        viewModelScope.launch {
            val newStatus = !_isOpen.value
            _updateState.value = UpdateState.Loading
            settingsRepository.updateOpenStatus(newStatus)
                .onSuccess { restaurant ->
                    _isOpen.value = restaurant.isOpen
                    _updateState.value = UpdateState.Success
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "Failed to update status")
                }
        }
    }
    
    fun toggleBusyMode() {
        viewModelScope.launch {
            val newStatus = !_isBusy.value
            _updateState.value = UpdateState.Loading
            settingsRepository.updateBusyMode(newStatus)
                .onSuccess { restaurant ->
                    _isBusy.value = restaurant.isBusy
                    _updateState.value = UpdateState.Success
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "Failed to update busy mode")
                }
        }
    }
    
    fun saveBusinessInfo() {
        viewModelScope.launch {
            val restaurant = currentRestaurant ?: return@launch
            _updateState.value = UpdateState.Loading
            val form = _formState.value
            val updated = restaurant.copy(
                businessName = form.businessName,
                phone = form.phone,
                address = restaurant.address.copy(
                    street = form.street,
                    city = form.city,
                    area = form.area,
                    postalCode = form.postalCode
                )
            )
            settingsRepository.updateRestaurantProfile(updated)
                .onSuccess {
                    currentRestaurant = it
                    _uiState.value = SettingsUiState.Success(it)
                    _updateState.value = UpdateState.Success
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "Failed to save")
                }
        }
    }
    
    fun saveDeliverySettings() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            val form = _formState.value
            val settings = DeliverySettings(
                minimumOrder = form.minimumOrder.toDoubleOrNull() ?: 0.0,
                deliveryRadius = form.deliveryRadius.toDoubleOrNull() ?: 5.0,
                estimatedPrepTime = form.estimatedPrepTime.toIntOrNull() ?: 30,
                packagingCharge = form.packagingCharge.toDoubleOrNull() ?: 0.0
            )
            settingsRepository.updateDeliverySettings(settings)
                .onSuccess { restaurant ->
                    currentRestaurant = restaurant
                    _uiState.value = SettingsUiState.Success(restaurant)
                    _updateState.value = UpdateState.Success
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "Failed to save")
                }
        }
    }
    
    fun updateOperatingHours(hours: OperatingHours) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            settingsRepository.updateOperatingHours(hours)
                .onSuccess { restaurant ->
                    _operatingHours.value = restaurant.operatingHours
                    _updateState.value = UpdateState.Success
                }
                .onFailure { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "Failed to update hours")
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    
    fun clearUpdateState() {
        _updateState.value = UpdateState.Idle
    }
}

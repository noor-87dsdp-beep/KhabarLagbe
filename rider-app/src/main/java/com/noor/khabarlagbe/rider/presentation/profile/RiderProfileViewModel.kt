package com.noor.khabarlagbe.rider.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.VehicleType
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val rider: Rider) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

data class ProfileEditState(
    val isEditing: Boolean = false,
    val editingSection: ProfileSection? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null
)

enum class ProfileSection {
    PERSONAL_INFO, VEHICLE_INFO, DOCUMENTS, BANK_DETAILS, SETTINGS
}

@HiltViewModel
class RiderProfileViewModel @Inject constructor(
    private val authRepository: RiderAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState.asStateFlow()
    
    val currentRider = authRepository.currentRider
    
    init {
        loadProfile()
    }
    
    private fun loadProfile() {
        viewModelScope.launch {
            authRepository.currentRider.collect { rider ->
                if (rider != null) {
                    _uiState.value = ProfileUiState.Success(rider)
                } else {
                    _uiState.value = ProfileUiState.Error("Profile not found")
                }
            }
        }
    }
    
    fun startEditing(section: ProfileSection) {
        _editState.value = _editState.value.copy(
            isEditing = true,
            editingSection = section,
            saveError = null
        )
    }
    
    fun cancelEditing() {
        _editState.value = ProfileEditState()
    }
    
    fun savePersonalInfo(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            
            // In a real implementation, this would call the repository
            // For now, just simulate success
            kotlinx.coroutines.delay(1000)
            
            _editState.value = ProfileEditState()
        }
    }
    
    fun saveVehicleInfo(vehicleType: VehicleType, vehicleNumber: String, licenseNumber: String) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            
            kotlinx.coroutines.delay(1000)
            
            _editState.value = ProfileEditState()
        }
    }
    
    fun saveBankDetails(
        bankAccountName: String?,
        bankAccountNumber: String?,
        bankName: String?,
        bikashNumber: String?,
        nagadNumber: String?
    ) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            
            kotlinx.coroutines.delay(1000)
            
            _editState.value = ProfileEditState()
        }
    }
    
    fun uploadDocument(documentType: String, filePath: String) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            
            kotlinx.coroutines.delay(2000)
            
            _editState.value = ProfileEditState()
        }
    }
    
    fun uploadProfilePhoto(filePath: String) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isSaving = true)
            
            kotlinx.coroutines.delay(2000)
            
            _editState.value = ProfileEditState()
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    
    fun refreshProfile() {
        loadProfile()
    }
}

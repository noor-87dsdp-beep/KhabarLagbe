package com.noor.khabarlagbe.rider.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.rider.domain.model.*
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiderProfileViewModel @Inject constructor(
    private val profileRepository: RiderProfileRepository,
    private val authRepository: RiderAuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val _rider = MutableStateFlow<Rider?>(null)
    val rider: StateFlow<Rider?> = _rider.asStateFlow()
    
    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle: StateFlow<Vehicle?> = _vehicle.asStateFlow()
    
    private val _documents = MutableStateFlow<List<RiderDocument>>(emptyList())
    val documents: StateFlow<List<RiderDocument>> = _documents.asStateFlow()
    
    private val _bankDetails = MutableStateFlow<BankDetails?>(null)
    val bankDetails: StateFlow<BankDetails?> = _bankDetails.asStateFlow()
    
    private val _stats = MutableStateFlow<DeliveryStats?>(null)
    val stats: StateFlow<DeliveryStats?> = _stats.asStateFlow()
    
    init {
        loadProfile()
    }
    
    private fun loadProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading
                
                profileRepository.getRiderProfile()
                    .onSuccess { profile ->
                        _rider.value = profile
                    }
                
                profileRepository.getVehicleDetails()
                    .onSuccess { vehicleData ->
                        _vehicle.value = vehicleData
                    }
                
                profileRepository.getDocuments()
                    .onSuccess { docs ->
                        _documents.value = docs
                    }
                
                profileRepository.getBankDetails()
                    .onSuccess { bank ->
                        _bankDetails.value = bank
                    }
                
                profileRepository.getDeliveryStats()
                    .onSuccess { statistics ->
                        _stats.value = statistics
                    }
                
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "প্রোফাইল লোড করতে ব্যর্থ")
            }
        }
    }
    
    fun updateProfile(name: String, email: String?, phone: String) {
        viewModelScope.launch {
            profileRepository.updateProfile(name, email, phone)
                .onSuccess { updatedRider ->
                    _rider.value = updatedRider
                    _uiState.value = ProfileUiState.UpdateSuccess
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Error("প্রোফাইল আপডেট করতে ব্যর্থ")
                }
        }
    }
    
    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            profileRepository.updateVehicleDetails(vehicle)
                .onSuccess { updatedVehicle ->
                    _vehicle.value = updatedVehicle
                    _uiState.value = ProfileUiState.UpdateSuccess
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Error("গাড়ির তথ্য আপডেট করতে ব্যর্থ")
                }
        }
    }
    
    fun updateBankDetails(bankDetails: BankDetails) {
        viewModelScope.launch {
            profileRepository.updateBankDetails(bankDetails)
                .onSuccess { updated ->
                    _bankDetails.value = updated
                    _uiState.value = ProfileUiState.UpdateSuccess
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Error("ব্যাংক তথ্য আপডেট করতে ব্যর্থ")
                }
        }
    }
    
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Error("লগআউট করতে ব্যর্থ")
                }
        }
    }
    
    fun refresh() {
        loadProfile()
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    object UpdateSuccess : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

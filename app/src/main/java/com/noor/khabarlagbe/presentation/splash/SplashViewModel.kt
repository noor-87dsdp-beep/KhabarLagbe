package com.noor.khabarlagbe.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.data.local.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SplashScreen
 * Checks user authentication status
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val token = appPreferences.getAuthTokenSync()
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }
}

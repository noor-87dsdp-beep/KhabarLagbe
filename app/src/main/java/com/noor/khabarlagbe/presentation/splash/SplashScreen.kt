package com.noor.khabarlagbe.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.navigation.Screen
import kotlinx.coroutines.delay

/**
 * SplashScreen - Initial screen shown when app launches
 * Displays app logo with animation and navigates to appropriate screen
 */
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(key1 = true) {
        // Scale animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                delayMillis = 100
            )
        )
        
        // Fade in animation
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        
        // Wait for 2 seconds
        delay(2000L)
        
        // Navigate based on login status
        val destination = if (isLoggedIn) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }
        
        navController.navigate(destination) {
            // Remove splash from back stack
            popUpTo(Screen.Splash.route) {
                inclusive = true
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = "KhabarLagbe Logo",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}

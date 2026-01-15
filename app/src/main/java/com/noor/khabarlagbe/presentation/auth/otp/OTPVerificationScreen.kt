package com.noor.khabarlagbe.presentation.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.presentation.components.PrimaryButton
import com.noor.khabarlagbe.presentation.components.KhabarLagbeTextButton
import com.noor.khabarlagbe.ui.theme.Error
import com.noor.khabarlagbe.ui.theme.Primary
import com.noor.khabarlagbe.ui.theme.Success

/**
 * OTP Verification Screen
 * 
 * Features:
 * - 6-digit OTP input with auto-focus between fields
 * - Resend OTP button with 60-second countdown timer
 * - Verify button with loading state
 * - Error display for invalid OTP
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPVerificationScreen(
    phoneNumber: String,
    navController: NavController,
    viewModel: OTPViewModel = hiltViewModel(),
    onVerificationSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val focusManager = LocalFocusManager.current

    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    // Set phone number on first composition
    LaunchedEffect(phoneNumber) {
        viewModel.setPhoneNumber(phoneNumber)
        viewModel.startCountdown()
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is OTPUiState.Success -> {
                onVerificationSuccess()
            }
            is OTPUiState.OtpResent -> {
                // Reset to idle after showing resent message
                kotlinx.coroutines.delay(2000)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify OTP") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Phone icon
            Icon(
                imageVector = Icons.Filled.PhoneAndroid,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enter Verification Code",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We've sent a 6-digit code to",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Input Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                otpValues.forEachIndexed { index, value ->
                    OTPInputField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                otpValues = otpValues.toMutableList().apply {
                                    this[index] = newValue
                                }

                                // Auto-focus next field
                                if (newValue.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                } else if (newValue.isEmpty() && index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                }

                                // Clear error when user types
                                if (uiState is OTPUiState.Error) {
                                    viewModel.resetState()
                                }
                            }
                        },
                        focusRequester = focusRequesters[index],
                        isError = uiState is OTPUiState.Error,
                        imeAction = if (index == 5) ImeAction.Done else ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                val otp = otpValues.joinToString("")
                                if (otp.length == 6) {
                                    viewModel.verifyOtp(otp)
                                }
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            if (uiState is OTPUiState.Error) {
                Text(
                    text = (uiState as OTPUiState.Error).message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Error,
                    textAlign = TextAlign.Center
                )
            }

            // Success message for OTP resent
            if (uiState is OTPUiState.OtpResent) {
                Text(
                    text = "OTP sent successfully!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Success,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Verify Button
            PrimaryButton(
                text = "Verify OTP",
                onClick = {
                    focusManager.clearFocus()
                    val otp = otpValues.joinToString("")
                    viewModel.verifyOtp(otp)
                },
                isLoading = uiState is OTPUiState.Loading,
                enabled = otpValues.all { it.isNotEmpty() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resend OTP
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Didn't receive code?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(4.dp))

                if (countdown > 0) {
                    Text(
                        text = "Resend in ${countdown}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    KhabarLagbeTextButton(
                        text = "Resend",
                        onClick = { viewModel.resendOtp() },
                        enabled = countdown == 0 && uiState !is OTPUiState.Loading,
                        modifier = Modifier.height(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Single OTP input field component
 */
@Composable
private fun OTPInputField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    isError: Boolean,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .size(56.dp)
            .focusRequester(focusRequester)
            .border(
                width = 2.dp,
                color = when {
                    isError -> Error
                    value.isNotEmpty() -> Primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            ),
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = if (isError) Error else MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
                innerTextField()
            }
        }
    )
}

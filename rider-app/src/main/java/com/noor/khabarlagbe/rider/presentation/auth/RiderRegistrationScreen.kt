package com.noor.khabarlagbe.rider.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.rider.domain.model.VehicleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderRegistrationScreen(
    navController: NavController,
    viewModel: RiderAuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val registrationStep by viewModel.registrationStep.collectAsState()
    val registrationData by viewModel.registrationData.collectAsState()
    
    var showOtpDialog by remember { mutableStateOf(false) }
    var otpPhone by remember { mutableStateOf("") }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is AuthUiState.RequiresOtp -> {
                otpPhone = (uiState as AuthUiState.RequiresOtp).phone
                showOtpDialog = true
            }
            else -> {}
        }
    }
    
    if (showOtpDialog) {
        OtpVerificationDialog(
            phone = otpPhone,
            onVerify = { otp ->
                viewModel.verifyOtp(otp)
                showOtpDialog = false
            },
            onResend = { viewModel.resendOtp() },
            onDismiss = { 
                showOtpDialog = false
                viewModel.resetState()
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রেজিস্ট্রেশন") },
                navigationIcon = {
                    IconButton(onClick = {
                        when (registrationStep) {
                            RegistrationStep.PersonalInfo -> {
                                viewModel.resetRegistration()
                                navController.popBackStack()
                            }
                            else -> viewModel.previousRegistrationStep()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress indicator
            RegistrationProgressIndicator(currentStep = registrationStep)
            
            // Step content
            when (registrationStep) {
                RegistrationStep.PersonalInfo -> PersonalInfoStep(
                    data = registrationData,
                    onUpdate = viewModel::updateRegistrationData,
                    onNext = viewModel::nextRegistrationStep,
                    isLoading = uiState is AuthUiState.Loading
                )
                RegistrationStep.VehicleInfo -> VehicleInfoStep(
                    data = registrationData,
                    onUpdate = viewModel::updateRegistrationData,
                    onNext = viewModel::nextRegistrationStep,
                    isLoading = uiState is AuthUiState.Loading
                )
                RegistrationStep.Documents -> DocumentsStep(
                    data = registrationData,
                    onUpdate = viewModel::updateRegistrationData,
                    onNext = viewModel::nextRegistrationStep,
                    isLoading = uiState is AuthUiState.Loading
                )
                RegistrationStep.BankDetails -> BankDetailsStep(
                    data = registrationData,
                    onUpdate = viewModel::updateRegistrationData,
                    onSubmit = viewModel::register,
                    isLoading = uiState is AuthUiState.Loading,
                    error = (uiState as? AuthUiState.Error)?.message
                )
            }
        }
    }
}

@Composable
private fun RegistrationProgressIndicator(currentStep: RegistrationStep) {
    val steps = listOf(
        "ব্যক্তিগত" to (currentStep.ordinal >= RegistrationStep.PersonalInfo.ordinal),
        "যানবাহন" to (currentStep.ordinal >= RegistrationStep.VehicleInfo.ordinal),
        "ডকুমেন্ট" to (currentStep.ordinal >= RegistrationStep.Documents.ordinal),
        "ব্যাংক" to (currentStep.ordinal >= RegistrationStep.BankDetails.ordinal)
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (label, isActive) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PersonalInfoStep(
    data: RegistrationData,
    onUpdate: (RegistrationData) -> Unit,
    onNext: () -> Unit,
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "ব্যক্তিগত তথ্য",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "আপনার সঠিক তথ্য দিন",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = data.name,
            onValueChange = { onUpdate(data.copy(name = it)) },
            label = { Text("পুরো নাম *") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.phone,
            onValueChange = { onUpdate(data.copy(phone = it)) },
            label = { Text("ফোন নম্বর *") },
            placeholder = { Text("01XXXXXXXXX") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.email,
            onValueChange = { onUpdate(data.copy(email = it)) },
            label = { Text("ইমেইল (ঐচ্ছিক)") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.password,
            onValueChange = { onUpdate(data.copy(password = it)) },
            label = { Text("পাসওয়ার্ড *") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.confirmPassword,
            onValueChange = { onUpdate(data.copy(confirmPassword = it)) },
            label = { Text("পাসওয়ার্ড নিশ্চিত করুন *") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            isError = data.confirmPassword.isNotEmpty() && data.password != data.confirmPassword,
            supportingText = if (data.confirmPassword.isNotEmpty() && data.password != data.confirmPassword) {
                { Text("পাসওয়ার্ড মিলছে না") }
            } else null
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = data.name.isNotBlank() && data.phone.isNotBlank() && 
                     data.password.isNotBlank() && data.password == data.confirmPassword &&
                     !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("পরবর্তী", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleInfoStep(
    data: RegistrationData,
    onUpdate: (RegistrationData) -> Unit,
    onNext: () -> Unit,
    isLoading: Boolean
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "যানবাহন তথ্য",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "আপনার যানবাহনের তথ্য দিন",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Vehicle type dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = getVehicleTypeName(data.vehicleType),
                onValueChange = {},
                readOnly = true,
                label = { Text("যানবাহনের ধরন *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                VehicleType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(getVehicleTypeName(type.name)) },
                        onClick = {
                            onUpdate(data.copy(vehicleType = type.name))
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.vehicleNumber,
            onValueChange = { onUpdate(data.copy(vehicleNumber = it.uppercase())) },
            label = { Text("গাড়ির নম্বর *") },
            placeholder = { Text("ঢাকা মেট্রো-১২-৩৪৫৬") },
            leadingIcon = { Icon(Icons.Default.DirectionsBike, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.licenseNumber,
            onValueChange = { onUpdate(data.copy(licenseNumber = it.uppercase())) },
            label = { Text("ড্রাইভিং লাইসেন্স নম্বর *") },
            leadingIcon = { Icon(Icons.Default.Badge, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.nidNumber,
            onValueChange = { onUpdate(data.copy(nidNumber = it)) },
            label = { Text("জাতীয় পরিচয়পত্র নম্বর (NID) *") },
            leadingIcon = { Icon(Icons.Default.CreditCard, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = data.vehicleNumber.isNotBlank() && data.licenseNumber.isNotBlank() && 
                     data.nidNumber.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("পরবর্তী", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, null)
        }
    }
}

@Composable
private fun DocumentsStep(
    data: RegistrationData,
    onUpdate: (RegistrationData) -> Unit,
    onNext: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "ডকুমেন্ট আপলোড",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "প্রয়োজনীয় ডকুমেন্ট আপলোড করুন (পরে করতে পারবেন)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        DocumentUploadCard(
            title = "প্রোফাইল ছবি",
            description = "স্পষ্ট মুখের ছবি আপলোড করুন",
            isUploaded = data.profilePhotoUrl.isNotEmpty(),
            onClick = { /* Open image picker */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DocumentUploadCard(
            title = "NID সামনের পাতা",
            description = "জাতীয় পরিচয়পত্রের সামনের ছবি",
            isUploaded = data.nidFrontUrl.isNotEmpty(),
            onClick = { /* Open image picker */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DocumentUploadCard(
            title = "NID পেছনের পাতা",
            description = "জাতীয় পরিচয়পত্রের পেছনের ছবি",
            isUploaded = data.nidBackUrl.isNotEmpty(),
            onClick = { /* Open image picker */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DocumentUploadCard(
            title = "ড্রাইভিং লাইসেন্স",
            description = "আপনার ড্রাইভিং লাইসেন্সের ছবি",
            isUploaded = data.licenseUrl.isNotEmpty(),
            onClick = { /* Open image picker */ }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("পরবর্তী", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, null)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("পরে আপলোড করব")
        }
    }
}

@Composable
private fun DocumentUploadCard(
    title: String,
    description: String,
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isUploaded) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                       else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = if (isUploaded) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BankDetailsStep(
    data: RegistrationData,
    onUpdate: (RegistrationData) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "ব্যাংক ও পেমেন্ট তথ্য",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "আপনার পেমেন্ট গ্রহণের তথ্য দিন (ঐচ্ছিক)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // bKash
        OutlinedTextField(
            value = data.bikashNumber,
            onValueChange = { onUpdate(data.copy(bikashNumber = it)) },
            label = { Text("বিকাশ নম্বর") },
            placeholder = { Text("01XXXXXXXXX") },
            leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nagad
        OutlinedTextField(
            value = data.nagadNumber,
            onValueChange = { onUpdate(data.copy(nagadNumber = it)) },
            label = { Text("নগদ নম্বর") },
            placeholder = { Text("01XXXXXXXXX") },
            leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "ব্যাংক অ্যাকাউন্ট (ঐচ্ছিক)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.bankName,
            onValueChange = { onUpdate(data.copy(bankName = it)) },
            label = { Text("ব্যাংকের নাম") },
            leadingIcon = { Icon(Icons.Default.AccountBalance, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.bankAccountName,
            onValueChange = { onUpdate(data.copy(bankAccountName = it)) },
            label = { Text("অ্যাকাউন্ট হোল্ডারের নাম") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = data.bankAccountNumber,
            onValueChange = { onUpdate(data.copy(bankAccountNumber = it)) },
            label = { Text("অ্যাকাউন্ট নম্বর") },
            leadingIcon = { Icon(Icons.Default.CreditCard, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
        
        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("রেজিস্ট্রেশন সম্পন্ন করুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun getVehicleTypeName(type: String): String {
    return when (type.uppercase()) {
        "BICYCLE" -> "সাইকেল"
        "MOTORCYCLE" -> "মোটরসাইকেল"
        "SCOOTER" -> "স্কুটার"
        "CAR" -> "গাড়ি"
        else -> type
    }
}

private val RegistrationStep.ordinal: Int
    get() = when (this) {
        RegistrationStep.PersonalInfo -> 0
        RegistrationStep.VehicleInfo -> 1
        RegistrationStep.Documents -> 2
        RegistrationStep.BankDetails -> 3
    }

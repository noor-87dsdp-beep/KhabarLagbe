package com.noor.khabarlagbe.restaurant.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantRegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: RestaurantAuthViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val registrationState by viewModel.registrationState.collectAsState()
    
    LaunchedEffect(registrationState) {
        if (registrationState is AuthUiState.Success) {
            onRegistrationSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রেস্টুরেন্ট রেজিস্ট্রেশন") },
                navigationIcon = {
                    IconButton(onClick = {
                        when (currentStep) {
                            RegistrationStep.BusinessInfo -> onBackClick()
                            else -> viewModel.previousStep()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            LinearProgressIndicator(
                progress = {
                    when (currentStep) {
                        RegistrationStep.BusinessInfo -> 0.25f
                        RegistrationStep.Location -> 0.5f
                        RegistrationStep.Documents -> 0.75f
                        RegistrationStep.BankDetails -> 1f
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Step indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StepIndicator("১", "ব্যবসা", currentStep == RegistrationStep.BusinessInfo)
                StepIndicator("২", "ঠিকানা", currentStep == RegistrationStep.Location)
                StepIndicator("৩", "ডকুমেন্ট", currentStep == RegistrationStep.Documents)
                StepIndicator("৪", "ব্যাংক", currentStep == RegistrationStep.BankDetails)
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (currentStep) {
                    RegistrationStep.BusinessInfo -> BusinessInfoStep(formState, viewModel)
                    RegistrationStep.Location -> LocationStep(formState, viewModel)
                    RegistrationStep.Documents -> DocumentsStep(formState, viewModel)
                    RegistrationStep.BankDetails -> BankDetailsStep(formState, viewModel, registrationState)
                }
            }
            
            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentStep != RegistrationStep.BusinessInfo) {
                    OutlinedButton(
                        onClick = { viewModel.previousStep() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("পিছনে")
                    }
                }
                
                Button(
                    onClick = {
                        if (currentStep == RegistrationStep.BankDetails) {
                            viewModel.register()
                        } else {
                            viewModel.nextStep()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = registrationState !is AuthUiState.Loading
                ) {
                    if (registrationState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(if (currentStep == RegistrationStep.BankDetails) "রেজিস্ট্রেশন করুন" else "পরবর্তী")
                    }
                }
            }
        }
    }
}

@Composable
fun StepIndicator(number: String, label: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = number,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BusinessInfoStep(formState: RegistrationFormState, viewModel: RestaurantAuthViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "ব্যবসার তথ্য",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = formState.businessName,
            onValueChange = { viewModel.updateFormState { copy(businessName = it) } },
            label = { Text("রেস্টুরেন্টের নাম *") },
            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.ownerName,
            onValueChange = { viewModel.updateFormState { copy(ownerName = it) } },
            label = { Text("মালিকের নাম *") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.email,
            onValueChange = { viewModel.updateFormState { copy(email = it) } },
            label = { Text("ইমেইল *") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.phone,
            onValueChange = { viewModel.updateFormState { copy(phone = it) } },
            label = { Text("ফোন নম্বর *") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.password,
            onValueChange = { viewModel.updateFormState { copy(password = it) } },
            label = { Text("পাসওয়ার্ড *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.confirmPassword,
            onValueChange = { viewModel.updateFormState { copy(confirmPassword = it) } },
            label = { Text("পাসওয়ার্ড নিশ্চিত করুন *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = formState.confirmPassword.isNotEmpty() && formState.password != formState.confirmPassword
        )
    }
}

@Composable
fun LocationStep(formState: RegistrationFormState, viewModel: RestaurantAuthViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "রেস্টুরেন্টের ঠিকানা",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = formState.street,
            onValueChange = { viewModel.updateFormState { copy(street = it) } },
            label = { Text("রাস্তার ঠিকানা *") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = formState.area,
                onValueChange = { viewModel.updateFormState { copy(area = it) } },
                label = { Text("এলাকা *") },
                modifier = Modifier.weight(1f)
            )
            
            OutlinedTextField(
                value = formState.city,
                onValueChange = { viewModel.updateFormState { copy(city = it) } },
                label = { Text("শহর *") },
                modifier = Modifier.weight(1f)
            )
        }
        
        OutlinedTextField(
            value = formState.postalCode,
            onValueChange = { viewModel.updateFormState { copy(postalCode = it) } },
            label = { Text("পোস্টাল কোড") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "মানচিত্রে লোকেশন সিলেক্ট করুন",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Open map picker */ }) {
                    Icon(Icons.Default.MyLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("লোকেশন নির্বাচন")
                }
            }
        }
    }
}

@Composable
fun DocumentsStep(formState: RegistrationFormState, viewModel: RestaurantAuthViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "প্রয়োজনীয় ডকুমেন্ট",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "নিম্নলিখিত ডকুমেন্টগুলো আপলোড করুন",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        DocumentUploadCard(
            title = "ট্রেড লাইসেন্স",
            description = "বৈধ ট্রেড লাইসেন্সের কপি",
            isUploaded = formState.tradeLicense.isNotEmpty(),
            onUpload = { viewModel.updateFormState { copy(tradeLicense = "uploaded") } }
        )
        
        DocumentUploadCard(
            title = "জাতীয় পরিচয়পত্র (সামনে)",
            description = "মালিকের NID এর সামনের অংশ",
            isUploaded = formState.nidFront.isNotEmpty(),
            onUpload = { viewModel.updateFormState { copy(nidFront = "uploaded") } }
        )
        
        DocumentUploadCard(
            title = "জাতীয় পরিচয়পত্র (পিছনে)",
            description = "মালিকের NID এর পিছনের অংশ",
            isUploaded = formState.nidBack.isNotEmpty(),
            onUpload = { viewModel.updateFormState { copy(nidBack = "uploaded") } }
        )
        
        DocumentUploadCard(
            title = "রেস্টুরেন্টের ছবি",
            description = "রেস্টুরেন্টের বাইরের ছবি",
            isUploaded = formState.restaurantPhoto.isNotEmpty(),
            onUpload = { viewModel.updateFormState { copy(restaurantPhoto = "uploaded") } }
        )
    }
}

@Composable
fun DocumentUploadCard(
    title: String,
    description: String,
    isUploaded: Boolean,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                contentDescription = null,
                tint = if (isUploaded) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isUploaded) {
                Text(
                    text = "আপলোড হয়েছে",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF4CAF50)
                )
            } else {
                OutlinedButton(onClick = onUpload) {
                    Text("আপলোড")
                }
            }
        }
    }
}

@Composable
fun BankDetailsStep(
    formState: RegistrationFormState,
    viewModel: RestaurantAuthViewModel,
    registrationState: AuthUiState
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "ব্যাংক অ্যাকাউন্ট তথ্য",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "পেমেন্ট পেতে ব্যাংক অ্যাকাউন্টের তথ্য দিন",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        OutlinedTextField(
            value = formState.bankName,
            onValueChange = { viewModel.updateFormState { copy(bankName = it) } },
            label = { Text("ব্যাংকের নাম *") },
            leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.accountName,
            onValueChange = { viewModel.updateFormState { copy(accountName = it) } },
            label = { Text("অ্যাকাউন্ট নাম *") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.accountNumber,
            onValueChange = { viewModel.updateFormState { copy(accountNumber = it) } },
            label = { Text("অ্যাকাউন্ট নম্বর *") },
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.branchName,
            onValueChange = { viewModel.updateFormState { copy(branchName = it) } },
            label = { Text("শাখার নাম *") },
            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = formState.routingNumber,
            onValueChange = { viewModel.updateFormState { copy(routingNumber = it) } },
            label = { Text("রাউটিং নম্বর (ঐচ্ছিক)") },
            leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (registrationState is AuthUiState.Error) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = registrationState.message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

package com.noor.khabarlagbe.rider.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
    var currentStep by remember { mutableStateOf(0) }
    
    // Step 1: Personal Info
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Step 2: Vehicle Details
    var selectedVehicleType by remember { mutableStateOf(VehicleType.MOTORCYCLE) }
    var vehicleMake by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    
    // Step 3: Documents
    var nidNumber by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রাইডার নিবন্ধন") },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (currentStep > 0) currentStep-- else navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = (currentStep + 1) / 4f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    text = "ধাপ ${currentStep + 1} / 4",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = when (currentStep) {
                        0 -> "ব্যক্তিগত তথ্য"
                        1 -> "গাড়ির বিবরণ"
                        2 -> "ডকুমেন্ট"
                        else -> "ব্যাংক বিবরণ"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                when (currentStep) {
                    0 -> PersonalInfoStep(
                        name, { name = it },
                        phone, { phone = it },
                        email, { email = it },
                        password, { password = it },
                        confirmPassword, { confirmPassword = it },
                        passwordVisible, { passwordVisible = it }
                    )
                    1 -> VehicleDetailsStep(
                        selectedVehicleType, { selectedVehicleType = it },
                        vehicleMake, { vehicleMake = it },
                        vehicleModel, { vehicleModel = it },
                        plateNumber, { plateNumber = it }
                    )
                    2 -> DocumentsStep(
                        nidNumber, { nidNumber = it },
                        licenseNumber, { licenseNumber = it }
                    )
                    3 -> BankDetailsStep()
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("পূর্ববর্তী")
                        }
                    }
                    
                    Button(
                        onClick = {
                            if (currentStep < 3) {
                                currentStep++
                            } else {
                                viewModel.register(
                                    name, phone, email, password, confirmPassword,
                                    selectedVehicleType.name, vehicleMake, vehicleModel,
                                    plateNumber, nidNumber, licenseNumber
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = uiState !is AuthUiState.Loading
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(if (currentStep < 3) "পরবর্তী" else "নিবন্ধন করুন")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalInfoStep(
    name: String, onNameChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean, onPasswordVisibleChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("পূর্ণ নাম *") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("ফোন নম্বর *") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("১১ ডিজিটের নম্বর দিন") }
        )
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("ইমেইল (ঐচ্ছিক)") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("পাসওয়ার্ড *") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("কমপক্ষে ৬ অক্ষর") }
        )
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("পাসওয়ার্ড নিশ্চিত করুন *") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsStep(
    selectedType: VehicleType, onTypeChange: (VehicleType) -> Unit,
    make: String, onMakeChange: (String) -> Unit,
    model: String, onModelChange: (String) -> Unit,
    plateNumber: String, onPlateNumberChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = when (selectedType) {
                    VehicleType.BICYCLE -> "সাইকেল"
                    VehicleType.MOTORCYCLE -> "মোটরসাইকেল"
                    VehicleType.SCOOTER -> "স্কুটার"
                    VehicleType.CAR -> "গাড়ি"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("গাড়ির ধরন *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                VehicleType.values().forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (type) {
                                    VehicleType.BICYCLE -> "সাইকেল"
                                    VehicleType.MOTORCYCLE -> "মোটরসাইকেল"
                                    VehicleType.SCOOTER -> "স্কুটার"
                                    VehicleType.CAR -> "গাড়ি"
                                }
                            )
                        },
                        onClick = {
                            onTypeChange(type)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        OutlinedTextField(
            value = make,
            onValueChange = onMakeChange,
            label = { Text("ব্র্যান্ড (যেমন: Hero, Honda)") },
            leadingIcon = { Icon(Icons.Default.DirectionsBike, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = model,
            onValueChange = onModelChange,
            label = { Text("মডেল") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = plateNumber,
            onValueChange = onPlateNumberChange,
            label = { Text("নম্বর প্লেট *") },
            leadingIcon = { Icon(Icons.Default.Numbers, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("গাড়ির রেজিস্ট্রেশন নম্বর") }
        )
    }
}

@Composable
fun DocumentsStep(
    nidNumber: String, onNidNumberChange: (String) -> Unit,
    licenseNumber: String, onLicenseNumberChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = nidNumber,
            onValueChange = onNidNumberChange,
            label = { Text("এনআইডি নম্বর *") },
            leadingIcon = { Icon(Icons.Default.Badge, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = licenseNumber,
            onValueChange = onLicenseNumberChange,
            label = { Text("ড্রাইভিং লাইসেন্স নম্বর *") },
            leadingIcon = { Icon(Icons.Default.ContactPage, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📄 ডকুমেন্ট আপলোড",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "নিবন্ধনের পরে আপনি আপনার প্রোফাইল থেকে নিম্নলিখিত ডকুমেন্ট আপলোড করতে পারবেন:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• এনআইডি কার্ডের ছবি", style = MaterialTheme.typography.bodySmall)
                Text("• ড্রাইভিং লাইসেন্সের ছবি", style = MaterialTheme.typography.bodySmall)
                Text("• গাড়ির রেজিস্ট্রেশন সার্টিফিকেট", style = MaterialTheme.typography.bodySmall)
                Text("• আপনার ছবি", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun BankDetailsStep() {
    var accountName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var branchName by remember { mutableStateOf("") }
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "💳 ব্যাংক বিবরণ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "আপনার আয় সরাসরি আপনার ব্যাংক অ্যাকাউন্টে পাঠানো হবে। আপনি পরবর্তীতে প্রোফাইল থেকে ব্যাংক বিবরণ যোগ করতে পারবেন।",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        OutlinedTextField(
            value = accountName,
            onValueChange = { accountName = it },
            label = { Text("অ্যাকাউন্ট হোল্ডারের নাম (ঐচ্ছিক)") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = accountNumber,
            onValueChange = { accountNumber = it },
            label = { Text("অ্যাকাউন্ট নম্বর (ঐচ্ছিক)") },
            leadingIcon = { Icon(Icons.Default.AccountBalance, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = bankName,
            onValueChange = { bankName = it },
            label = { Text("ব্যাংকের নাম (ঐচ্ছিক)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        OutlinedTextField(
            value = branchName,
            onValueChange = { branchName = it },
            label = { Text("শাখার নাম (ঐচ্ছিক)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

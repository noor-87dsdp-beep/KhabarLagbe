package com.noor.khabarlagbe.restaurant.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantSettingsScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val isOpen by viewModel.isOpen.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditBusinessDialog by remember { mutableStateOf(false) }
    var showEditDeliveryDialog by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateState.Success -> {
                snackbarHostState.showSnackbar("সেভ হয়েছে")
                viewModel.clearUpdateState()
            }
            is UpdateState.Error -> {
                snackbarHostState.showSnackbar((updateState as UpdateState.Error).message)
                viewModel.clearUpdateState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সেটিংস") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (uiState) {
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SettingsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as SettingsUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadSettings() }) {
                            Text("আবার চেষ্টা করুন")
                        }
                    }
                }
            }
            is SettingsUiState.Success -> {
                val restaurant = (uiState as SettingsUiState.Success).restaurant
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status section
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "রেস্টুরেন্ট স্ট্যাটাস",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = if (isOpen) Color(0xFF4CAF50) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("রেস্টুরেন্ট খোলা/বন্ধ")
                                        Text(
                                            text = if (isOpen) "অর্ডার গ্রহণ করা হচ্ছে" else "অর্ডার বন্ধ",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = isOpen,
                                    onCheckedChange = { viewModel.toggleOpenStatus() }
                                )
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (isBusy) Color(0xFFFF9800) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("ব্যস্ত মোড")
                                        Text(
                                            text = if (isBusy) "বেশি সময় লাগছে" else "স্বাভাবিক",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = isBusy,
                                    onCheckedChange = { viewModel.toggleBusyMode() }
                                )
                            }
                        }
                    }
                    
                    // Business info section
                    SettingsSection(
                        title = "ব্যবসার তথ্য",
                        icon = Icons.Default.Business,
                        onClick = { showEditBusinessDialog = true }
                    ) {
                        SettingsItem(
                            label = "রেস্টুরেন্টের নাম",
                            value = restaurant.businessName
                        )
                        SettingsItem(
                            label = "ফোন",
                            value = restaurant.phone
                        )
                        SettingsItem(
                            label = "ঠিকানা",
                            value = "${restaurant.address.street}, ${restaurant.address.area}"
                        )
                    }
                    
                    // Delivery settings section
                    SettingsSection(
                        title = "ডেলিভারি সেটিংস",
                        icon = Icons.Default.DeliveryDining,
                        onClick = { showEditDeliveryDialog = true }
                    ) {
                        SettingsItem(
                            label = "মিনিমাম অর্ডার",
                            value = "৳${restaurant.deliverySettings?.minimumOrder?.toInt() ?: 0}"
                        )
                        SettingsItem(
                            label = "ডেলিভারি রেডিয়াস",
                            value = "${restaurant.deliverySettings?.deliveryRadius ?: 5} কিমি"
                        )
                        SettingsItem(
                            label = "আনুমানিক প্রস্তুতির সময়",
                            value = "${restaurant.deliverySettings?.estimatedPrepTime ?: 30} মিনিট"
                        )
                        SettingsItem(
                            label = "প্যাকেজিং চার্জ",
                            value = "৳${restaurant.deliverySettings?.packagingCharge?.toInt() ?: 0}"
                        )
                    }
                    
                    // Notifications section
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = null)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "নোটিফিকেশন",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("নোটিফিকেশন চালু")
                                Switch(
                                    checked = formState.notificationsEnabled,
                                    onCheckedChange = { 
                                        viewModel.updateFormState { copy(notificationsEnabled = it) }
                                    }
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("সাউন্ড")
                                Switch(
                                    checked = formState.soundEnabled,
                                    onCheckedChange = { 
                                        viewModel.updateFormState { copy(soundEnabled = it) }
                                    }
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ভাইব্রেশন")
                                Switch(
                                    checked = formState.vibrationEnabled,
                                    onCheckedChange = { 
                                        viewModel.updateFormState { copy(vibrationEnabled = it) }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Logout button
                    OutlinedButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("লগআউট")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Edit business info dialog
    if (showEditBusinessDialog) {
        AlertDialog(
            onDismissRequest = { showEditBusinessDialog = false },
            title = { Text("ব্যবসার তথ্য সম্পাদনা") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = formState.businessName,
                        onValueChange = { viewModel.updateFormState { copy(businessName = it) } },
                        label = { Text("রেস্টুরেন্টের নাম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formState.phone,
                        onValueChange = { viewModel.updateFormState { copy(phone = it) } },
                        label = { Text("ফোন") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formState.street,
                        onValueChange = { viewModel.updateFormState { copy(street = it) } },
                        label = { Text("ঠিকানা") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formState.area,
                            onValueChange = { viewModel.updateFormState { copy(area = it) } },
                            label = { Text("এলাকা") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = formState.city,
                            onValueChange = { viewModel.updateFormState { copy(city = it) } },
                            label = { Text("শহর") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveBusinessInfo()
                        showEditBusinessDialog = false
                    }
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBusinessDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
    
    // Edit delivery settings dialog
    if (showEditDeliveryDialog) {
        AlertDialog(
            onDismissRequest = { showEditDeliveryDialog = false },
            title = { Text("ডেলিভারি সেটিংস সম্পাদনা") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = formState.minimumOrder,
                        onValueChange = { viewModel.updateFormState { copy(minimumOrder = it) } },
                        label = { Text("মিনিমাম অর্ডার (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formState.deliveryRadius,
                        onValueChange = { viewModel.updateFormState { copy(deliveryRadius = it) } },
                        label = { Text("ডেলিভারি রেডিয়াস (কিমি)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formState.estimatedPrepTime,
                        onValueChange = { viewModel.updateFormState { copy(estimatedPrepTime = it) } },
                        label = { Text("প্রস্তুতির সময় (মিনিট)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formState.packagingCharge,
                        onValueChange = { viewModel.updateFormState { copy(packagingCharge = it) } },
                        label = { Text("প্যাকেজিং চার্জ (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveDeliverySettings()
                        showEditDeliveryDialog = false
                    }
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDeliveryDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("লগআউট") },
            text = { Text("আপনি কি নিশ্চিত যে লগআউট করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("লগআউট")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SettingsItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

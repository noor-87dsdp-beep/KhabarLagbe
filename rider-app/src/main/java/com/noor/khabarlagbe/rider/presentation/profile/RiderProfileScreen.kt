package com.noor.khabarlagbe.rider.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.rider.domain.model.Rider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderProfileScreen(
    navController: NavController,
    viewModel: RiderProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val editState by viewModel.editState.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("প্রোফাইল") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Logout")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is ProfileUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text((uiState as ProfileUiState.Error).message)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refreshProfile() }) { Text("আবার চেষ্টা করুন") }
                }
            }
            
            is ProfileUiState.Success -> {
                val rider = (uiState as ProfileUiState.Success).rider
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile header
                    ProfileHeader(rider = rider, onEditPhoto = { viewModel.uploadProfilePhoto("") })
                    
                    // Stats summary
                    StatsSummaryCard(rider = rider)
                    
                    // Profile sections
                    ProfileSection(
                        title = "ব্যক্তিগত তথ্য",
                        icon = Icons.Default.Person,
                        onClick = { viewModel.startEditing(ProfileSection.PERSONAL_INFO) }
                    ) {
                        ProfileItem(label = "নাম", value = rider.name)
                        ProfileItem(label = "ফোন", value = rider.phone)
                        ProfileItem(label = "ইমেইল", value = rider.email ?: "যোগ করা হয়নি")
                    }
                    
                    ProfileSection(
                        title = "যানবাহন তথ্য",
                        icon = Icons.Default.DirectionsBike,
                        onClick = { viewModel.startEditing(ProfileSection.VEHICLE_INFO) }
                    ) {
                        ProfileItem(label = "যানবাহনের ধরন", value = getVehicleTypeName(rider.vehicleType.name))
                        ProfileItem(label = "গাড়ির নম্বর", value = rider.vehicleNumber)
                        ProfileItem(label = "লাইসেন্স নম্বর", value = rider.licenseNumber)
                    }
                    
                    ProfileSection(
                        title = "ডকুমেন্ট",
                        icon = Icons.Default.Description,
                        onClick = { viewModel.startEditing(ProfileSection.DOCUMENTS) }
                    ) {
                        ProfileItem(label = "NID", value = "••••${rider.nidNumber.takeLast(4)}")
                        ProfileItem(label = "স্ট্যাটাস", value = "যাচাইকৃত", valueColor = Color(0xFF4CAF50))
                    }
                    
                    ProfileSection(
                        title = "পেমেন্ট তথ্য",
                        icon = Icons.Default.AccountBalanceWallet,
                        onClick = { viewModel.startEditing(ProfileSection.BANK_DETAILS) }
                    ) {
                        ProfileItem(label = "বিকাশ/নগদ", value = "কনফিগার করা আছে")
                        ProfileItem(label = "ব্যাংক", value = "যোগ করা হয়নি")
                    }
                    
                    ProfileSection(
                        title = "সেটিংস",
                        icon = Icons.Default.Settings,
                        onClick = { viewModel.startEditing(ProfileSection.SETTINGS) }
                    ) {
                        ProfileItem(label = "নোটিফিকেশন", value = "সক্রিয়")
                        ProfileItem(label = "ভাষা", value = "বাংলা")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("লগআউট") },
            text = { Text("আপনি কি লগআউট করতে চান?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
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
private fun ProfileHeader(
    rider: Rider,
    onEditPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            if (rider.profileImageUrl != null) {
                AsyncImage(
                    model = rider.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        Icons.Default.Person, null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
            
            FloatingActionButton(
                onClick = onEditPhoto,
                modifier = Modifier.size(32.dp).align(Alignment.BottomEnd),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.CameraAlt, null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = rider.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = rider.phone,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rider.rating.toInt()) Icons.Default.Star 
                                 else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format("%.1f", rider.rating),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatsSummaryCard(rider: Rider) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatSummaryItem(value = "${rider.totalDeliveries}", label = "ডেলিভারি", Color.White)
            StatSummaryItem(value = "৳${rider.todayEarnings.toInt()}", label = "আজ", Color.White)
            StatSummaryItem(value = "৳${rider.weeklyEarnings.toInt()}", label = "এই সপ্তাহ", Color.White)
        }
    }
}

@Composable
private fun StatSummaryItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ProfileSection(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon, null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    Icons.Default.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun ProfileItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
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

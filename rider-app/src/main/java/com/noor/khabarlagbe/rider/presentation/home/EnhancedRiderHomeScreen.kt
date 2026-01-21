package com.noor.khabarlagbe.rider.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.navigation.RiderScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedRiderHomeScreen(
    navController: NavController,
    viewModel: RiderHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val currentRider by viewModel.currentRider.collectAsState(initial = null)
    val activeOrder by viewModel.activeOrder.collectAsState(initial = null)
    val todayEarnings by viewModel.todayEarnings.collectAsState(initial = 0.0)
    
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DeliveryDining,
                            null,
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("KhabarLagbe Rider")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Badge(containerColor = Color.Red) {
                            Icon(Icons.Default.Notifications, "Notifications")
                        }
                    }
                    IconButton(onClick = { navController.navigate(RiderScreen.Profile.route) }) {
                        if (currentRider?.profileImageUrl != null) {
                            AsyncImage(
                                model = currentRider!!.profileImageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.AccountCircle, "Profile")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("হোম") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                        navController.navigate(RiderScreen.Earnings.route)
                    },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, "Earnings") },
                    label = { Text("আয়") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { 
                        selectedTab = 2
                        navController.navigate(RiderScreen.History.route)
                    },
                    icon = { Icon(Icons.Default.History, "History") },
                    label = { Text("ইতিহাস") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { 
                        selectedTab = 3
                        navController.navigate(RiderScreen.Profile.route)
                    },
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("প্রোফাইল") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Online/Offline Toggle
            item {
                EnhancedOnlineToggleCard(
                    isOnline = isOnline,
                    rider = currentRider,
                    onToggle = { viewModel.toggleOnlineStatus() }
                )
            }
            
            // Active delivery card (if any)
            if (activeOrder != null) {
                item {
                    ActiveDeliveryCard(
                        order = activeOrder!!,
                        onClick = { navController.navigate(RiderScreen.Delivery.createRoute(activeOrder!!.id)) }
                    )
                }
            }
            
            // Today's Stats
            item {
                EnhancedTodayStatsCard(
                    earnings = todayEarnings,
                    deliveries = currentRider?.totalDeliveries ?: 0,
                    rating = currentRider?.rating ?: 0.0,
                    onStatsClick = { navController.navigate(RiderScreen.Stats.route) }
                )
            }
            
            // Quick Actions
            item {
                QuickActionsCard(
                    onEarningsClick = { navController.navigate(RiderScreen.Earnings.route) },
                    onHistoryClick = { navController.navigate(RiderScreen.History.route) },
                    onStatsClick = { navController.navigate(RiderScreen.Stats.route) }
                )
            }
            
            // Available Orders Section
            if (isOnline) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "উপলব্ধ অর্ডার",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { navController.navigate(RiderScreen.AvailableOrders.route) }) {
                            Text("সব দেখুন")
                            Icon(Icons.Default.ArrowForward, null, Modifier.size(16.dp))
                        }
                    }
                }
                
                // Sample available orders (in production, this would come from ViewModel)
                items(3) { index ->
                    SampleOrderCard(
                        orderNumber = "#${1234 + index}",
                        restaurantName = "রেস্টুরেন্ট ${index + 1}",
                        distance = "${(2.0 + index * 0.5)}",
                        earnings = "${60 + index * 5}",
                        onAccept = { navController.navigate(RiderScreen.Delivery.createRoute("${1234 + index}")) },
                        onReject = { }
                    )
                }
            } else {
                item {
                    OfflinePromptCard()
                }
            }
        }
    }
}

@Composable
private fun EnhancedOnlineToggleCard(
    isOnline: Boolean,
    rider: Rider?,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFF757575)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isOnline) "আপনি অনলাইন" else "আপনি অফলাইন",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOnline) "অর্ডার গ্রহণ করছেন" else "অনলাইন হয়ে অর্ডার নিন",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.WifiTethering else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp).size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFF757575)
                )
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.PowerSettingsNew else Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOnline) "অফলাইন হন" else "অনলাইন হন",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActiveDeliveryCard(
    order: RiderOrder,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF2196F3)
            ) {
                Icon(
                    Icons.Default.LocalShipping, null,
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "সক্রিয় ডেলিভারি",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = order.restaurantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "→ ${order.customerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ChevronRight, null,
                tint = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
private fun EnhancedTodayStatsCard(
    earnings: Double,
    deliveries: Int,
    rating: Double,
    onStatsClick: () -> Unit
) {
    Card(
        onClick = onStatsClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "আজকের পরিসংখ্যান",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "আয়",
                    value = "৳${earnings.toInt()}",
                    icon = Icons.Default.AttachMoney,
                    iconTint = Color(0xFF4CAF50)
                )
                StatItem(
                    label = "ডেলিভারি",
                    value = "$deliveries",
                    icon = Icons.Default.LocalShipping,
                    iconTint = Color(0xFF2196F3)
                )
                StatItem(
                    label = "রেটিং",
                    value = String.format("%.1f", rating),
                    icon = Icons.Default.Star,
                    iconTint = Color(0xFFFFB300)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionsCard(
    onEarningsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionItem(
            icon = Icons.Default.AccountBalanceWallet,
            label = "আয়",
            color = Color(0xFF4CAF50),
            onClick = onEarningsClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            icon = Icons.Default.History,
            label = "ইতিহাস",
            color = Color(0xFF2196F3),
            onClick = onHistoryClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionItem(
            icon = Icons.Default.BarChart,
            label = "পারফরম্যান্স",
            color = Color(0xFF9C27B0),
            onClick = onStatsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Icon(
                    icon, null,
                    tint = color,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun SampleOrderCard(
    orderNumber: String,
    restaurantName: String,
    distance: String,
    earnings: String,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = orderNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "30s",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, null, tint = Color(0xFFFF5722), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(restaurantName)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Route, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$distance কিমি")
                }
                Text(
                    text = "আয়: ৳$earnings",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                    Text("বাতিল")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("গ্রহণ")
                }
            }
        }
    }
}

@Composable
private fun OfflinePromptCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.WifiOff, null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "অনলাইন হয়ে অর্ডার দেখুন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "উপরের বাটনে ক্লিক করে অনলাইন হন",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

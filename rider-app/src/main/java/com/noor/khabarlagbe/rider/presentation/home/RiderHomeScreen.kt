package com.noor.khabarlagbe.rider.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.rider.navigation.RiderScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderHomeScreen(
    navController: NavController,
    viewModel: RiderHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val rider by viewModel.rider.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val availableOrders by viewModel.availableOrders.collectAsState()
    val activeOrder by viewModel.activeOrder.collectAsState()
    
    val isRefreshing = uiState is HomeUiState.Loading
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KhabarLagbe Rider", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { /* Navigate to notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { navController.navigate(RiderScreen.Profile.route) }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("হোম") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(RiderScreen.Earnings.route) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Earnings") },
                    label = { Text("আয়") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(RiderScreen.History.route) },
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("ইতিহাস") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(RiderScreen.Profile.route) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("প্রোফাইল") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Online/Offline Toggle
                item {
                    OnlineToggleCard(
                        isOnline = isOnline,
                        onToggle = { viewModel.toggleOnlineStatus() }
                    )
                }
                
                // Today's Stats
                item {
                    TodayStatsCard(
                        todayEarnings = rider?.todayEarnings ?: 0.0,
                        totalDeliveries = rider?.totalDeliveries ?: 0,
                        rating = rider?.rating ?: 0.0
                    )
                }
                
                // Active Order
                if (activeOrder != null) {
                    item {
                        Text(
                            text = "সক্রিয় ডেলিভারি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        ActiveOrderCard(
                            order = activeOrder!!,
                            onClick = {
                                navController.navigate(RiderScreen.ActiveDelivery.createRoute(activeOrder!!.id))
                            }
                        )
                    }
                }
                
                // Available Orders
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
                            if (availableOrders.isNotEmpty()) {
                                TextButton(onClick = { navController.navigate(RiderScreen.AvailableOrders.route) }) {
                                    Text("সব দেখুন")
                                }
                            }
                        }
                    }
                    
                    if (availableOrders.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Inbox,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "কোন অর্ডার নেই",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "নতুন অর্ডার পেলে জানানো হবে",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        items(availableOrders.take(3)) { order ->
                            AvailableOrderCard(
                                orderNumber = "#${order.id.takeLast(6)}",
                                restaurantName = order.restaurantName,
                                distance = "${String.format("%.1f", order.distance)} কিমি",
                                earnings = "৳${order.deliveryFee.toInt()}",
                                onAccept = { viewModel.acceptOrder(order.id) },
                                onReject = { viewModel.rejectOrder(order.id) }
                            )
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "অনলাইন হয়ে অর্ডার দেখুন",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
                
                // Quick Actions
                item {
                    Text(
                        text = "দ্রুত অ্যাক্সেস",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    QuickActionsGrid(navController)
                }
            }
    }
}

@Composable
fun OnlineToggleCard(
    isOnline: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isOnline) "আপনি অনলাইন" else "আপনি অফলাইন",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
                )
            ) {
                Text(
                    text = if (isOnline) "অফলাইন হন" else "অনলাইন হন",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TodayStatsCard(
    todayEarnings: Double,
    totalDeliveries: Int,
    rating: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "আজকের পরিসংখ্যান",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "আয়", value = "৳${todayEarnings.toInt()}", icon = Icons.Default.AttachMoney)
                StatItem(label = "ডেলিভারি", value = "$totalDeliveries", icon = Icons.Default.LocalShipping)
                StatItem(label = "রেটিং", value = String.format("%.1f", rating), icon = Icons.Default.Star)
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
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
fun ActiveOrderCard(
    order: com.noor.khabarlagbe.rider.domain.model.RiderOrder,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "অর্ডার #${order.id.takeLast(6)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "৳${order.deliveryFee.toInt()}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${order.restaurantName} → ${order.customerName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("বিস্তারিত দেখুন")
            }
        }
    }
}

@Composable
fun AvailableOrderCard(
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                    text = "30 সেকেন্ড",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = restaurantName)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Route,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = distance)
                }
                
                Text(
                    text = "আয়: $earnings",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("বাতিল")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("গ্রহণ করুন")
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionCard(
            icon = Icons.Default.Assessment,
            title = "পরিসংখ্যান",
            onClick = { navController.navigate(RiderScreen.Stats.route) },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            icon = Icons.Default.AccountBalanceWallet,
            title = "আয়",
            onClick = { navController.navigate(RiderScreen.Earnings.route) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

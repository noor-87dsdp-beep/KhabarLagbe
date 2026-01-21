package com.noor.khabarlagbe.restaurant.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noor.khabarlagbe.restaurant.domain.model.Order
import com.noor.khabarlagbe.restaurant.domain.model.RestaurantStats
import com.noor.khabarlagbe.restaurant.presentation.components.OrderCard
import com.noor.khabarlagbe.restaurant.presentation.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    onNavigateToOrders: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: RestaurantDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isOpen by viewModel.isOpen.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("নতুন", "প্রস্তুত হচ্ছে", "প্রস্তুত")
    var selectedNavItem by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("KhabarLagbe Restaurant", style = MaterialTheme.typography.titleLarge) 
                        Text(
                            text = if (isOpen) "🟢 খোলা" else "🔴 বন্ধ",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOpen) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                },
                actions = {
                    // Open/Close toggle
                    Switch(
                        checked = isOpen,
                        onCheckedChange = { viewModel.toggleOpenStatus() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4CAF50),
                            checkedTrackColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
                        )
                    )
                    IconButton(onClick = { /* Navigate to notifications */ }) {
                        BadgedBox(
                            badge = {
                                val newOrderCount = when (uiState) {
                                    is DashboardUiState.Success -> (uiState as DashboardUiState.Success).newOrders.size
                                    else -> 0
                                }
                                if (newOrderCount > 0) {
                                    Badge { Text("$newOrderCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedNavItem == 0,
                    onClick = { selectedNavItem = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("ড্যাশবোর্ড") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 1,
                    onClick = { 
                        selectedNavItem = 1
                        onNavigateToOrders()
                    },
                    icon = { 
                        BadgedBox(
                            badge = {
                                val pendingCount = when (uiState) {
                                    is DashboardUiState.Success -> (uiState as DashboardUiState.Success).newOrders.size
                                    else -> 0
                                }
                                if (pendingCount > 0) {
                                    Badge { Text("$pendingCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = "Orders")
                        }
                    },
                    label = { Text("অর্ডার") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 2,
                    onClick = { 
                        selectedNavItem = 2
                        onNavigateToMenu()
                    },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Menu") },
                    label = { Text("মেনু") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 3,
                    onClick = { 
                        selectedNavItem = 3
                        onNavigateToReports()
                    },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                    label = { Text("বিশ্লেষণ") }
                )
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is DashboardUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardUiState.Success -> {
                    val state = uiState as DashboardUiState.Success
                    DashboardContent(
                        stats = state.stats,
                        newOrders = state.newOrders,
                        preparingOrders = state.preparingOrders,
                        readyOrders = state.readyOrders,
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        tabs = tabs,
                        isBusy = isBusy,
                        onToggleBusy = { viewModel.toggleBusyMode() },
                        onAcceptOrder = { viewModel.acceptOrder(it) },
                        onRejectOrder = { orderId, reason -> viewModel.rejectOrder(orderId, reason) },
                        onMarkPreparing = { viewModel.markPreparing(it) },
                        onMarkReady = { viewModel.markReady(it) },
                        onOrderClick = onOrderClick
                    )
                }
                is DashboardUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as DashboardUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadDashboard() }) {
                                Text("আবার চেষ্টা করুন")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    stats: RestaurantStats,
    newOrders: List<Order>,
    preparingOrders: List<Order>,
    readyOrders: List<Order>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>,
    isBusy: Boolean,
    onToggleBusy: () -> Unit,
    onAcceptOrder: (String) -> Unit,
    onRejectOrder: (String, String) -> Unit,
    onMarkPreparing: (String) -> Unit,
    onMarkReady: (String) -> Unit,
    onOrderClick: (String) -> Unit
) {
    var showRejectDialog by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Busy mode alert
        if (isBusy) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color(0xFFFF9800)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ব্যস্ত মোড চালু",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "অর্ডারে অতিরিক্ত সময় লাগছে",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        TextButton(onClick = onToggleBusy) {
                            Text("বন্ধ করুন")
                        }
                    }
                }
            }
        }
        
        // Today's Stats
        item {
            TodayStatsSection(stats = stats)
        }
        
        // Quick stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "অপেক্ষমান",
                    value = "${stats.pendingOrders}",
                    icon = Icons.Default.Pending,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "প্রস্তুত হচ্ছে",
                    value = "${stats.preparingOrders}",
                    icon = Icons.Default.Restaurant,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "প্রস্তুত",
                    value = "${stats.readyOrders}",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Tab selector
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    val count = when (index) {
                        0 -> newOrders.size
                        1 -> preparingOrders.size
                        2 -> readyOrders.size
                        else -> 0
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        text = { Text("$title ($count)") }
                    )
                }
            }
        }
        
        // Orders based on selected tab
        val orders = when (selectedTab) {
            0 -> newOrders
            1 -> preparingOrders
            2 -> readyOrders
            else -> emptyList()
        }
        
        if (orders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inbox,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো অর্ডার নেই",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onAccept = { onAcceptOrder(order.id) },
                    onReject = { showRejectDialog = order.id },
                    onMarkPreparing = { onMarkPreparing(order.id) },
                    onMarkReady = { onMarkReady(order.id) },
                    onClick = { onOrderClick(order.id) }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Reject dialog
    if (showRejectDialog != null) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = null },
            title = { Text("অর্ডার বাতিল") },
            text = {
                Column {
                    Text("অর্ডার বাতিলের কারণ লিখুন:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("কারণ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectOrder(showRejectDialog!!, rejectReason)
                        showRejectDialog = null
                        rejectReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("বাতিল করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRejectDialog = null
                    rejectReason = ""
                }) {
                    Text("পিছনে")
                }
            }
        )
    }
}

@Composable
fun TodayStatsSection(stats: RestaurantStats) {
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
                StatItem(
                    label = "অর্ডার",
                    value = "${stats.todayOrders}",
                    icon = Icons.Default.Receipt,
                    color = Color(0xFF2196F3)
                )
                StatItem(
                    label = "আয়",
                    value = "৳${stats.todayRevenue.toInt()}",
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    label = "রেটিং",
                    value = "%.1f".format(stats.averageRating),
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFC107)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
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

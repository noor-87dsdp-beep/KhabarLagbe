package com.noor.khabarlagbe.restaurant.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noor.khabarlagbe.restaurant.presentation.components.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOrderClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val showRejectDialog by viewModel.showRejectDialog.collectAsState()
    
    var rejectReason by remember { mutableStateOf("") }
    
    val tabs = listOf("নতুন", "প্রস্তুত হচ্ছে", "প্রস্তুত", "সম্পন্ন")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অর্ডার ম্যানেজমেন্ট") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    val count = when (uiState) {
                        is OrdersUiState.Success -> when (index) {
                            0 -> (uiState as OrdersUiState.Success).newOrders.size
                            1 -> (uiState as OrdersUiState.Success).preparingOrders.size
                            2 -> (uiState as OrdersUiState.Success).readyOrders.size
                            3 -> (uiState as OrdersUiState.Success).completedOrders.size
                            else -> 0
                        }
                        else -> 0
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { 
                            Text(
                                if (count > 0) "$title ($count)" else title
                            ) 
                        }
                    )
                }
            }
            
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState) {
                    is OrdersUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is OrdersUiState.Success -> {
                        val orders = when (selectedTab) {
                            0 -> (uiState as OrdersUiState.Success).newOrders
                            1 -> (uiState as OrdersUiState.Success).preparingOrders
                            2 -> (uiState as OrdersUiState.Success).readyOrders
                            3 -> (uiState as OrdersUiState.Success).completedOrders
                            else -> emptyList()
                        }
                        
                        if (orders.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Inbox,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "কোনো অর্ডার নেই",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(orders, key = { it.id }) { order ->
                                    OrderCard(
                                        order = order,
                                        onAccept = { viewModel.acceptOrder(order.id) },
                                        onReject = { viewModel.showRejectDialog(order.id) },
                                        onMarkPreparing = { viewModel.markPreparing(order.id) },
                                        onMarkReady = { viewModel.markReady(order.id) },
                                        onClick = { onOrderClick(order.id) }
                                    )
                                }
                            }
                        }
                    }
                    is OrdersUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = (uiState as OrdersUiState.Error).message,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadOrders() }) {
                                    Text("আবার চেষ্টা করুন")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showRejectDialog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRejectDialog() },
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
                        viewModel.rejectOrder(showRejectDialog!!, rejectReason)
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
                    viewModel.dismissRejectDialog()
                    rejectReason = ""
                }) {
                    Text("পিছনে")
                }
            }
        )
    }
}

package com.noor.khabarlagbe.rider.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHistoryScreen(
    navController: NavController,
    viewModel: DeliveryHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ডেলিভারি ইতিহাস") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("রেস্টুরেন্ট বা গ্রাহক খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HistoryFilter.values().forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = when (filter) {
                                    HistoryFilter.ALL -> "সব"
                                    HistoryFilter.TODAY -> "আজ"
                                    HistoryFilter.THIS_WEEK -> "এই সপ্তাহ"
                                    HistoryFilter.THIS_MONTH -> "এই মাস"
                                }
                            )
                        }
                    )
                }
            }
            
            // Stats summary
            val stats = viewModel.getDeliveryStats()
            if (stats.totalDeliveries > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatSummaryItem(
                            value = "${stats.totalDeliveries}",
                            label = "ডেলিভারি"
                        )
                        StatSummaryItem(
                            value = "৳${stats.totalEarnings.toInt()}",
                            label = "আয়"
                        )
                        StatSummaryItem(
                            value = "${String.format("%.1f", stats.totalDistance)} কিমি",
                            label = "দূরত্ব"
                        )
                    }
                }
            }
            
            // Delivery list
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.weight(1f)
            ) {
                when (uiState) {
                    is DeliveryHistoryUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    is DeliveryHistoryUiState.Empty -> {
                        EmptyHistoryContent()
                    }
                    
                    is DeliveryHistoryUiState.Error -> {
                        ErrorContent(
                            message = (uiState as DeliveryHistoryUiState.Error).message,
                            onRetry = { viewModel.refresh() }
                        )
                    }
                    
                    is DeliveryHistoryUiState.Success -> {
                        val deliveries = (uiState as DeliveryHistoryUiState.Success).deliveries
                        val listState = rememberLazyListState()
                        
                        LaunchedEffect(listState.firstVisibleItemIndex) {
                            viewModel.loadMoreIfNeeded(listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size)
                        }
                        
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(deliveries, key = { it.id }) { delivery ->
                                DeliveryHistoryItem(delivery = delivery)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSummaryItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
private fun DeliveryHistoryItem(delivery: RiderOrder) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    
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
                    text = "#${delivery.id.takeLast(6)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "সম্পন্ন",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Restaurant, null,
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = delivery.restaurantName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person, null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = delivery.customerName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(Date(delivery.deliveredAt ?: delivery.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "৳${delivery.deliveryFee.toInt()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.History, null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "কোনো ডেলিভারি ইতিহাস নেই",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "আপনার ডেলিভারি সম্পন্ন হলে এখানে দেখা যাবে",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("আবার চেষ্টা করুন") }
    }
}

package com.noor.khabarlagbe.restaurant.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noor.khabarlagbe.restaurant.domain.model.Order
import com.noor.khabarlagbe.restaurant.domain.model.OrderStatusEnum
import com.noor.khabarlagbe.restaurant.presentation.components.OrderActionButtons
import com.noor.khabarlagbe.restaurant.presentation.components.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orderDetailState by viewModel.orderDetailState.collectAsState()
    
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
        viewModel.observeOrderDetail(orderId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অর্ডার বিস্তারিত") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (orderDetailState) {
            is OrderDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is OrderDetailUiState.Success -> {
                val order = (orderDetailState as OrderDetailUiState.Success).order
                OrderDetailContent(
                    order = order,
                    onAccept = { viewModel.acceptOrder(order.id) },
                    onReject = { viewModel.showRejectDialog(order.id) },
                    onMarkPreparing = { viewModel.markPreparing(order.id) },
                    onMarkReady = { viewModel.markReady(order.id) },
                    modifier = Modifier.padding(padding)
                )
            }
            is OrderDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (orderDetailState as OrderDetailUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadOrderDetail(orderId) }) {
                            Text("আবার চেষ্টা করুন")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailContent(
    order: Order,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onMarkPreparing: () -> Unit,
    onMarkReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Order header
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "অর্ডার #${order.orderNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    StatusChip(status = order.status)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = order.createdAt.take(16).replace("T", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Customer info
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "গ্রাহকের তথ্য",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(order.customerName)
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(order.customerPhone)
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${order.customerAddress.street}, ${order.customerAddress.area}, ${order.customerAddress.city}")
                }
            }
        }
        
        // Order items
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "অর্ডার আইটেম",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.name} x${item.quantity}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (!item.specialInstructions.isNullOrBlank()) {
                                Text(
                                    text = "নোট: ${item.specialInstructions}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            item.customizations?.forEach { customization ->
                                Text(
                                    text = "• ${customization.name}: ${customization.option}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = "৳${item.totalPrice.toInt()}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    HorizontalDivider()
                }
                
                if (!order.specialInstructions.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Note,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = order.specialInstructions,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        // Payment summary
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "পেমেন্ট সামারি",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                PaymentRow("সাবটোটাল", "৳${order.subtotal.toInt()}")
                PaymentRow("ডেলিভারি চার্জ", "৳${order.deliveryFee.toInt()}")
                PaymentRow("প্যাকেজিং চার্জ", "৳${order.packagingCharge.toInt()}")
                if (order.discount > 0) {
                    PaymentRow("ছাড়", "-৳${order.discount.toInt()}", color = Color(0xFF4CAF50))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                PaymentRow(
                    "মোট",
                    "৳${order.total.toInt()}",
                    isBold = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "পেমেন্ট পদ্ধতি",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = order.paymentMethod,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Timeline
        if (order.timeline.isNotEmpty()) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "অর্ডার টাইমলাইন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    order.timeline.forEach { event ->
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = OrderStatusEnum.fromString(event.status).displayNameBn(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = event.timestamp.take(16).replace("T", " "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!event.note.isNullOrBlank()) {
                                    Text(
                                        text = event.note,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Rider info
        if (order.riderId != null) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "রাইডার তথ্য",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(order.riderName ?: "রাইডার")
                            Text(
                                text = order.riderPhone ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        
        // Action buttons
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                OrderActionButtons(
                    status = order.status,
                    onAccept = onAccept,
                    onReject = onReject,
                    onMarkPreparing = onMarkPreparing,
                    onMarkReady = onMarkReady
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PaymentRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

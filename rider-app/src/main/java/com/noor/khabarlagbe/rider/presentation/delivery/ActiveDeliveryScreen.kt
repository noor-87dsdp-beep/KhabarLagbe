package com.noor.khabarlagbe.rider.presentation.delivery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.presentation.components.DeliveryStatusCard
import com.noor.khabarlagbe.rider.presentation.map.RiderDeliveryMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveDeliveryScreen(
    navController: NavController,
    viewModel: ActiveDeliveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentOrder by viewModel.currentOrder.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isUpdatingStatus by viewModel.isUpdatingStatus.collectAsState()
    val context = LocalContext.current
    
    var showOrderDetailsSheet by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState) {
        if (uiState is ActiveDeliveryUiState.Completed) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    currentOrder?.let {
                        Text("ডেলিভারি #${it.id.takeLast(6)}")
                    } ?: Text("ডেলিভারি")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    if (currentOrder != null) {
                        IconButton(onClick = { showOrderDetailsSheet = true }) {
                            Icon(Icons.Default.Info, "Order Details")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { padding ->
        when (uiState) {
            is ActiveDeliveryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is ActiveDeliveryUiState.NoActiveDelivery -> {
                NoActiveDeliveryContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onFindOrders = { navController.navigate("available_orders") }
                )
            }
            
            is ActiveDeliveryUiState.Error -> {
                ErrorContent(
                    message = (uiState as ActiveDeliveryUiState.Error).message,
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onRetry = { viewModel.refresh() }
                )
            }
            
            is ActiveDeliveryUiState.Success -> {
                val successState = uiState as ActiveDeliveryUiState.Success
                val order = successState.order
                
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    // Map takes 60% of screen
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.6f)) {
                            RiderDeliveryMap(
                                currentLatitude = currentLocation?.latitude ?: 23.7937,
                                currentLongitude = currentLocation?.longitude ?: 90.4066,
                                restaurantLatitude = order.restaurantLocation.latitude,
                                restaurantLongitude = order.restaurantLocation.longitude,
                                customerLatitude = order.deliveryLocation.latitude,
                                customerLongitude = order.deliveryLocation.longitude,
                                isPickedUp = order.status.ordinal >= OrderStatus.PICKED_UP.ordinal,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        
                        // Order details take 40%
                        Card(
                            modifier = Modifier.weight(0.4f).fillMaxWidth(),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(20.dp)
                            ) {
                                // Status and destination
                                DeliveryStatusHeader(
                                    order = order,
                                    distance = successState.distanceToDestination,
                                    estimatedTime = successState.estimatedTime
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Contact buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val isGoingToRestaurant = order.status == OrderStatus.ACCEPTED || 
                                                            order.status == OrderStatus.ARRIVED_AT_RESTAURANT
                                    
                                    OutlinedButton(
                                        onClick = {
                                            val phone = if (isGoingToRestaurant) "" else order.customerPhone
                                            if (phone.isNotEmpty()) {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                                context.startActivity(intent)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Phone, null, Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("কল করুন")
                                    }
                                    
                                    OutlinedButton(
                                        onClick = {
                                            val destination = if (isGoingToRestaurant) 
                                                order.restaurantLocation else order.deliveryLocation
                                            val uri = Uri.parse(
                                                "google.navigation:q=${destination.latitude},${destination.longitude}"
                                            )
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            intent.setPackage("com.google.android.apps.maps")
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Navigation, null, Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("নেভিগেট")
                                    }
                                }
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                // Action button
                                viewModel.getNextAction()?.let { (label, action) ->
                                    Button(
                                        onClick = action,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        enabled = !isUpdatingStatus,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = getStatusButtonColor(order.status)
                                        )
                                    ) {
                                        if (isUpdatingStatus) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White
                                            )
                                        } else {
                                            Icon(
                                                imageVector = getStatusButtonIcon(order.status),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(label, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            is ActiveDeliveryUiState.Completed -> {
                // Will navigate away
            }
        }
    }
    
    // Order details bottom sheet
    if (showOrderDetailsSheet && currentOrder != null) {
        OrderDetailsBottomSheet(
            order = currentOrder!!,
            onDismiss = { showOrderDetailsSheet = false }
        )
    }
}

@Composable
private fun DeliveryStatusHeader(
    order: com.noor.khabarlagbe.rider.domain.model.RiderOrder,
    distance: Double,
    estimatedTime: Int
) {
    val isGoingToRestaurant = order.status == OrderStatus.ACCEPTED || 
                              order.status == OrderStatus.ARRIVED_AT_RESTAURANT
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getStatusText(order.status),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = getStatusColor(order.status)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF4CAF50).copy(alpha = 0.1f)
            ) {
                Text(
                    text = "৳${order.totalAmount.toInt()}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isGoingToRestaurant) Icons.Default.Restaurant else Icons.Default.Person,
                contentDescription = null,
                tint = if (isGoingToRestaurant) Color(0xFFFF5722) else Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isGoingToRestaurant) order.restaurantName else order.customerName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isGoingToRestaurant) order.restaurantAddress else order.deliveryAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Route, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f কিমি", distance),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "$estimatedTime মিনিট",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailsBottomSheet(
    order: com.noor.khabarlagbe.rider.domain.model.RiderOrder,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            Text(
                text = "অর্ডার বিবরণ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Restaurant info
            Text("রেস্টুরেন্ট", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(order.restaurantName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(order.restaurantAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Customer info
            Text("গ্রাহক", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(order.customerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(order.deliveryAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(order.customerPhone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Items
            Text("অর্ডার আইটেম", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.quantity}x ${item.name}")
                    Text("৳${(item.price * item.quantity).toInt()}")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("মোট", fontWeight = FontWeight.Bold)
                Text("৳${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ডেলিভারি ফি")
                Text("৳${order.deliveryFee.toInt()}", color = Color(0xFF4CAF50))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NoActiveDeliveryContent(
    modifier: Modifier = Modifier,
    onFindOrders: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocalShipping,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "কোনো সক্রিয় ডেলিভারি নেই",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onFindOrders) {
            Text("অর্ডার খুঁজুন")
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) { Text("আবার চেষ্টা করুন") }
    }
}

private fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.ACCEPTED -> "রেস্টুরেন্টে যান"
        OrderStatus.ARRIVED_AT_RESTAURANT -> "অর্ডারের জন্য অপেক্ষা করুন"
        OrderStatus.PICKED_UP -> "গ্রাহকের কাছে ডেলিভারি করুন"
        OrderStatus.ON_THE_WAY -> "ডেলিভারির পথে"
        OrderStatus.DELIVERED -> "ডেলিভারি সম্পন্ন!"
        else -> status.name
    }
}

private fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.ACCEPTED -> Color(0xFFFF5722)
        OrderStatus.ARRIVED_AT_RESTAURANT -> Color(0xFFFF9800)
        OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> Color(0xFF2196F3)
        OrderStatus.DELIVERED -> Color(0xFF4CAF50)
        else -> Color.Gray
    }
}

private fun getStatusButtonColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.ACCEPTED -> Color(0xFFFF5722)
        OrderStatus.ARRIVED_AT_RESTAURANT -> Color(0xFFFF9800)
        OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> Color(0xFF4CAF50)
        else -> Color(0xFF4CAF50)
    }
}

private fun getStatusButtonIcon(status: OrderStatus): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        OrderStatus.ACCEPTED -> Icons.Default.Restaurant
        OrderStatus.ARRIVED_AT_RESTAURANT -> Icons.Default.CheckCircle
        OrderStatus.PICKED_UP -> Icons.Default.LocalShipping
        OrderStatus.ON_THE_WAY -> Icons.Default.TaskAlt
        else -> Icons.Default.Check
    }
}

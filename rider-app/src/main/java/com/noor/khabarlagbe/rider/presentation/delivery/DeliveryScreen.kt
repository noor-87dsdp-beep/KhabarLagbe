package com.noor.khabarlagbe.rider.presentation.delivery

import android.Manifest
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.presentation.map.RiderDeliveryMap

/**
 * Delivery Screen for Rider App
 * Shows full-screen map with delivery route and order details overlay
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DeliveryScreen(
    orderId: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Location permissions
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Sample order data - in production this would come from ViewModel
    var orderStatus by remember { mutableStateOf(OrderStatus.ACCEPTED) }
    val restaurantName = "Spicy Delight Restaurant"
    val restaurantAddress = "45 Dhanmondi, Road 27, Dhaka"
    val customerName = "Ahmed Hassan"
    val customerAddress = "123 Gulshan Avenue, Block A, Dhaka"
    val customerPhone = "+880 1712-345678"
    val orderTotal = "৳850"
    
    // Sample coordinates for Dhaka - in production these come from real GPS and order data
    var currentLatitude by remember { mutableDoubleStateOf(23.7937) }
    var currentLongitude by remember { mutableDoubleStateOf(90.4066) }
    val restaurantLatitude = 23.7520
    val restaurantLongitude = 90.3758
    val customerLatitude = 23.7925
    val customerLongitude = 90.4078
    
    // Simulate location updates (in production, use LocationService)
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            // Simulate movement towards destination
            if (orderStatus == OrderStatus.ACCEPTED || orderStatus == OrderStatus.ARRIVED_AT_RESTAURANT) {
                currentLatitude += (restaurantLatitude - currentLatitude) * 0.1
                currentLongitude += (restaurantLongitude - currentLongitude) * 0.1
            } else if (orderStatus == OrderStatus.PICKED_UP || orderStatus == OrderStatus.ON_THE_WAY) {
                currentLatitude += (customerLatitude - currentLatitude) * 0.1
                currentLongitude += (customerLongitude - currentLongitude) * 0.1
            }
        }
    }
    
    // Request permissions if not granted
    LaunchedEffect(Unit) {
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery #$orderId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Full-screen map
            if (locationPermissions.allPermissionsGranted) {
                RiderDeliveryMap(
                    currentLatitude = currentLatitude,
                    currentLongitude = currentLongitude,
                    restaurantLatitude = restaurantLatitude,
                    restaurantLongitude = restaurantLongitude,
                    customerLatitude = customerLatitude,
                    customerLongitude = customerLongitude,
                    isPickedUp = orderStatus == OrderStatus.PICKED_UP || orderStatus == OrderStatus.ON_THE_WAY,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Permission request UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Location Permission Required",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please enable location access to use navigation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { locationPermissions.launchMultiplePermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
            
            // Bottom order details overlay
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (orderStatus) {
                                OrderStatus.ACCEPTED -> "Go to Restaurant"
                                OrderStatus.ARRIVED_AT_RESTAURANT -> "Arrived - Waiting for Order"
                                OrderStatus.PICKED_UP -> "Delivering to Customer"
                                OrderStatus.ON_THE_WAY -> "On The Way"
                                OrderStatus.DELIVERED -> "Delivered!"
                                else -> orderStatus.name
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (orderStatus) {
                                OrderStatus.DELIVERED -> Color(0xFF4CAF50)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        AssistChip(
                            onClick = { },
                            label = { Text(orderTotal) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                labelColor = Color(0xFF4CAF50)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Destination info based on status
                    if (orderStatus == OrderStatus.ACCEPTED || orderStatus == OrderStatus.ARRIVED_AT_RESTAURANT) {
                        // Restaurant info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Restaurant,
                                contentDescription = null,
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = restaurantName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = restaurantAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        // Customer info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = customerName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = customerAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { /* TODO: Call customer */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = "Call Customer",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Action Button
                    Button(
                        onClick = {
                            orderStatus = when (orderStatus) {
                                OrderStatus.ACCEPTED -> OrderStatus.ARRIVED_AT_RESTAURANT
                                OrderStatus.ARRIVED_AT_RESTAURANT -> OrderStatus.PICKED_UP
                                OrderStatus.PICKED_UP -> OrderStatus.ON_THE_WAY
                                OrderStatus.ON_THE_WAY -> OrderStatus.DELIVERED
                                else -> orderStatus
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (orderStatus) {
                                OrderStatus.ACCEPTED -> Color(0xFFFF5722)
                                OrderStatus.ARRIVED_AT_RESTAURANT -> Color(0xFFFF9800)
                                OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> Color(0xFF4CAF50)
                                OrderStatus.DELIVERED -> Color(0xFF9E9E9E)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        enabled = orderStatus != OrderStatus.DELIVERED
                    ) {
                        Icon(
                            imageVector = when (orderStatus) {
                                OrderStatus.ACCEPTED -> Icons.Filled.Restaurant
                                OrderStatus.ARRIVED_AT_RESTAURANT -> Icons.Filled.CheckCircle
                                OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY -> Icons.Filled.LocalShipping
                                OrderStatus.DELIVERED -> Icons.Filled.TaskAlt
                                else -> Icons.Filled.ArrowForward
                            },
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (orderStatus) {
                                OrderStatus.ACCEPTED -> "Arrived at Restaurant"
                                OrderStatus.ARRIVED_AT_RESTAURANT -> "Order Picked Up"
                                OrderStatus.PICKED_UP -> "Start Delivery"
                                OrderStatus.ON_THE_WAY -> "Complete Delivery"
                                OrderStatus.DELIVERED -> "Completed"
                                else -> "Next"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

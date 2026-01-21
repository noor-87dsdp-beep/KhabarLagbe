package com.noor.khabarlagbe.rider.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Rider Delivery Map Component
 * 
 * Full-screen map visualization for riders showing:
 * - Current location (GPS)
 * - Restaurant pickup location
 * - Customer delivery destination
 * - Navigation route
 * 
 * When Mapbox is configured (MAPBOX_DOWNLOADS_TOKEN set), this component
 * can be replaced with full Mapbox SDK integration for live map rendering.
 * 
 * @param currentLatitude Current rider GPS latitude
 * @param currentLongitude Current rider GPS longitude
 * @param restaurantLatitude Restaurant pickup latitude
 * @param restaurantLongitude Restaurant pickup longitude
 * @param customerLatitude Customer delivery latitude
 * @param customerLongitude Customer delivery longitude
 * @param showRoute Whether to display route visualization
 * @param isPickedUp Whether order has been picked up from restaurant
 */
@Composable
fun RiderDeliveryMap(
    currentLatitude: Double,
    currentLongitude: Double,
    restaurantLatitude: Double,
    restaurantLongitude: Double,
    customerLatitude: Double,
    customerLongitude: Double,
    showRoute: Boolean = true,
    isPickedUp: Boolean = false,
    modifier: Modifier = Modifier,
    onMapReady: () -> Unit = {}
) {
    // Calculate distances
    val distanceToRestaurant = calculateDistance(
        currentLatitude, currentLongitude,
        restaurantLatitude, restaurantLongitude
    )
    val distanceToCustomer = calculateDistance(
        if (isPickedUp) currentLatitude else restaurantLatitude,
        if (isPickedUp) currentLongitude else restaurantLongitude,
        customerLatitude, customerLongitude
    )
    
    // Trigger map ready callback
    LaunchedEffect(Unit) {
        onMapReady()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2).copy(alpha = 0.15f),
                        Color(0xFF4CAF50).copy(alpha = 0.1f),
                        Color(0xFF2196F3).copy(alpha = 0.15f)
                    )
                )
            )
    ) {
        // Map Grid Pattern (simulating map tiles)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            repeat(8) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    repeat(6) { col ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if ((row + col) % 2 == 0) 
                                        Color(0xFFE3F2FD).copy(alpha = 0.3f)
                                    else 
                                        Color(0xFFE8F5E9).copy(alpha = 0.3f)
                                )
                        )
                    }
                }
            }
        }
        
        // Route Visualization
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current location marker
            LocationMarker(
                icon = Icons.Filled.MyLocation,
                label = "আপনার অবস্থান",
                color = Color(0xFF4CAF50),
                isLarge = true
            )
            
            // Route line to first destination
            if (showRoute) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(4) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier.size(6.dp),
                            shape = CircleShape,
                            color = if (!isPickedUp) Color(0xFFFF5722) else Color(0xFF2196F3)
                        ) {}
                    }
                }
            }
            
            // Restaurant marker (if not picked up)
            if (!isPickedUp) {
                Spacer(modifier = Modifier.height(8.dp))
                LocationMarker(
                    icon = Icons.Filled.Restaurant,
                    label = "রেস্টুরেন্ট • ${String.format("%.1f", distanceToRestaurant)} কিমি",
                    color = Color(0xFFFF5722)
                )
                
                // Route line to customer
                if (showRoute) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(4) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                modifier = Modifier.size(6.dp),
                                shape = CircleShape,
                                color = Color(0xFF2196F3)
                            ) {}
                        }
                    }
                }
            }
            
            // Customer marker
            Spacer(modifier = Modifier.height(8.dp))
            LocationMarker(
                icon = Icons.Filled.Person,
                label = "গ্রাহক • ${String.format("%.1f", distanceToCustomer)} কিমি",
                color = Color(0xFF2196F3)
            )
        }
        
        // Navigation Legend
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "নেভিগেশন",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LegendItem(color = Color(0xFF4CAF50), label = "আপনার অবস্থান")
                if (!isPickedUp) {
                    LegendItem(color = Color(0xFFFF5722), label = "রেস্টুরেন্ট")
                }
                LegendItem(color = Color(0xFF2196F3), label = "গ্রাহক")
            }
        }
        
        // Distance Info Card
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Route,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format("%.1f কিমি", 
                        if (isPickedUp) distanceToCustomer 
                        else distanceToRestaurant + distanceToCustomer
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "মোট দূরত্ব",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        // GPS Coordinates Display
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "GPS: ${String.format("%.4f", currentLatitude)}, ${String.format("%.4f", currentLongitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LocationMarker(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    isLarge: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(if (isLarge) 56.dp else 48.dp),
            shape = CircleShape,
            color = color,
            shadowElevation = 4.dp
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(if (isLarge) 12.dp else 10.dp)
                    .size(if (isLarge) 32.dp else 28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.15f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = color,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = color
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Calculate distance between two coordinates using Haversine formula
 */
private fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val r = 6371.0 // Earth's radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

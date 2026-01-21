package com.noor.khabarlagbe.rider.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder

@Composable
fun DeliveryStatusCard(
    order: RiderOrder,
    distanceToDestination: Double,
    estimatedTime: Int,
    onNavigate: () -> Unit,
    onCallCustomer: () -> Unit,
    onCallRestaurant: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Status header
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
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "৳${order.totalAmount.toInt()}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress indicator
            DeliveryProgressIndicator(status = order.status)
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Destination info
            val isGoingToRestaurant = order.status == OrderStatus.ACCEPTED || 
                                      order.status == OrderStatus.ARRIVED_AT_RESTAURANT
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isGoingToRestaurant) Color(0xFFFF5722).copy(alpha = 0.1f)
                           else Color(0xFF2196F3).copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = if (isGoingToRestaurant) Icons.Default.Restaurant 
                                     else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isGoingToRestaurant) Color(0xFFFF5722) else Color(0xFF2196F3),
                        modifier = Modifier.padding(8.dp).size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isGoingToRestaurant) order.restaurantName else order.customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isGoingToRestaurant) order.restaurantAddress else order.deliveryAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Call button
                IconButton(
                    onClick = if (isGoingToRestaurant) onCallRestaurant else onCallCustomer
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Distance and time info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip(
                    icon = Icons.Default.Route,
                    value = String.format("%.1f কিমি", distanceToDestination),
                    label = "বাকি"
                )
                InfoChip(
                    icon = Icons.Default.Timer,
                    value = "$estimatedTime মিনিট",
                    label = "আনুমানিক"
                )
                InfoChip(
                    icon = Icons.Default.ShoppingBag,
                    value = "${order.items.size}টি",
                    label = "আইটেম"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigate button
            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("নেভিগেট করুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DeliveryProgressIndicator(status: OrderStatus) {
    val steps = listOf(
        "গ্রহণ" to (status.ordinal >= OrderStatus.ACCEPTED.ordinal),
        "রেস্টুরেন্ট" to (status.ordinal >= OrderStatus.ARRIVED_AT_RESTAURANT.ordinal),
        "পিকআপ" to (status.ordinal >= OrderStatus.PICKED_UP.ordinal),
        "ডেলিভারি" to (status == OrderStatus.DELIVERED)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (label, isCompleted) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCompleted) Color(0xFF4CAF50) 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(top = 16.dp)
                        .height(2.dp)
                        .background(
                            if (steps[index + 1].second) Color(0xFF4CAF50)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

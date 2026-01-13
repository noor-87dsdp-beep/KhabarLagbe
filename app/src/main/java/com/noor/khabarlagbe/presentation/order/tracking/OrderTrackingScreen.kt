package com.noor.khabarlagbe.presentation.order.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.domain.model.OrderStatus
import com.noor.khabarlagbe.domain.model.OrderTimelineEvent
import com.noor.khabarlagbe.ui.theme.Primary
import com.noor.khabarlagbe.ui.theme.Success
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // TODO: Replace with ViewModel
    val orderStatus = OrderStatus.ON_THE_WAY
    val timeline = getSampleTimeline()
    val riderName = "John Smith"
    val riderPhone = "+1 (555) 987-6543"
    val estimatedArrival = "15 min"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order ID
            item {
                Text(
                    text = "Order #$orderId",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Primary.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (orderStatus) {
                                OrderStatus.PENDING -> Icons.Filled.Schedule
                                OrderStatus.CONFIRMED -> Icons.Filled.CheckCircle
                                OrderStatus.PREPARING -> Icons.Filled.Restaurant
                                OrderStatus.READY_FOR_PICKUP -> Icons.Filled.Done
                                OrderStatus.PICKED_UP -> Icons.Filled.LocalShipping
                                OrderStatus.ON_THE_WAY -> Icons.Filled.LocalShipping
                                OrderStatus.DELIVERED -> Icons.Filled.TaskAlt
                                OrderStatus.CANCELLED -> Icons.Filled.Cancel
                            },
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = orderStatus.name.replace("_", " "),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estimated arrival: $estimatedArrival",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Rider Info (when order is picked up or on the way)
            if (orderStatus == OrderStatus.PICKED_UP || orderStatus == OrderStatus.ON_THE_WAY) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = "https://via.placeholder.com/60",
                                contentDescription = "Rider Photo",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = riderName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Delivery Partner",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { /* TODO: Call rider */ }) {
                                Icon(
                                    Icons.Filled.Phone,
                                    contentDescription = "Call",
                                    tint = Primary
                                )
                            }
                        }
                    }
                }
            }
            
            // Timeline
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Timeline",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            items(timeline) { event ->
                TimelineItem(event = event)
            }
        }
    }
}

@Composable
fun TimelineItem(event: OrderTimelineEvent) {
    val dateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Success
            )
            if (event != getSampleTimeline().last()) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Divider(modifier = Modifier.fillMaxHeight())
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.message,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dateFormat.format(Date(event.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getSampleTimeline() = listOf(
    OrderTimelineEvent(
        status = OrderStatus.ON_THE_WAY,
        timestamp = System.currentTimeMillis(),
        message = "Your order is on the way"
    ),
    OrderTimelineEvent(
        status = OrderStatus.PICKED_UP,
        timestamp = System.currentTimeMillis() - 10 * 60 * 1000,
        message = "Order picked up by delivery partner"
    ),
    OrderTimelineEvent(
        status = OrderStatus.PREPARING,
        timestamp = System.currentTimeMillis() - 20 * 60 * 1000,
        message = "Restaurant is preparing your order"
    ),
    OrderTimelineEvent(
        status = OrderStatus.CONFIRMED,
        timestamp = System.currentTimeMillis() - 25 * 60 * 1000,
        message = "Order confirmed by restaurant"
    ),
    OrderTimelineEvent(
        status = OrderStatus.PENDING,
        timestamp = System.currentTimeMillis() - 30 * 60 * 1000,
        message = "Order placed successfully"
    )
)

package com.noor.khabarlagbe.presentation.schedule

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringOrderScreen(
    navController: NavController,
    viewModel: ScheduleOrderViewModel = hiltViewModel()
) {
    val scheduledOrders by viewModel.scheduledOrders.collectAsState()
    val recurringOrders = scheduledOrders.filter { it.isRecurring }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Orders", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info card
            item {
                RecurringInfoCard()
            }

            // Frequency options
            item {
                FrequencyOptionsCard()
            }

            // Active recurring orders
            if (recurringOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Subscriptions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(recurringOrders) { order ->
                    RecurringOrderCard(
                        order = order,
                        onToggle = { /* Toggle active state */ },
                        onEdit = { /* Edit recurring order */ }
                    )
                }
            } else {
                item {
                    EmptyRecurringState()
                }
            }
        }
    }
}

@Composable
private fun RecurringInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Filled.AutoMode,
                null,
                tint = Success,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Automatic Ordering",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set it and forget it! Your favorite meals delivered on your schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    FeatureBadge("10% off", Icons.Filled.Percent)
                    Spacer(modifier = Modifier.width(8.dp))
                    FeatureBadge("Priority delivery", Icons.Filled.Speed)
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = Success.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Success, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = Success)
        }
    }
}

@Composable
private fun FrequencyOptionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Choose Frequency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FrequencyOption(
                    title = "Daily",
                    description = "Every day",
                    discount = "15% off",
                    modifier = Modifier.weight(1f)
                )
                FrequencyOption(
                    title = "Weekly",
                    description = "Once a week",
                    discount = "10% off",
                    isSelected = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FrequencyOption(
                    title = "Bi-weekly",
                    description = "Every 2 weeks",
                    discount = "5% off",
                    modifier = Modifier.weight(1f)
                )
                FrequencyOption(
                    title = "Monthly",
                    description = "Once a month",
                    discount = "Free delivery",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FrequencyOption(
    title: String,
    description: String,
    discount: String,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.1f) else SurfaceVariant
        ),
        border = if (isSelected) BorderStroke(2.dp, Primary) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = if (isSelected) Primary else Success.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = discount,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White else Success
                )
            }
        }
    }
}

@Composable
private fun RecurringOrderCard(
    order: ScheduledOrder,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    order.recurringPattern?.let { pattern ->
                        Text(
                            text = "${pattern.frequency.name.lowercase().replaceFirstChar { it.uppercase() }} at ${pattern.timeOfDay}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                
                Switch(
                    checked = order.status == ScheduledOrderStatus.SCHEDULED,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Days of week
                order.recurringPattern?.dayOfWeek?.let { days ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, day ->
                            val isActive = (index + 1) in days
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = if (isActive) Primary else SurfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isActive) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
                
                TextButton(onClick = onEdit) {
                    Text("Edit", color = Primary)
                }
            }
        }
    }
}

@Composable
private fun EmptyRecurringState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Repeat,
                null,
                tint = TextDisabled,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No recurring orders",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                text = "Set up automatic reorders for your favorites",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Create recurring order */ }) {
                Icon(Icons.Filled.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Subscription")
            }
        }
    }
}

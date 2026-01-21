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
import com.noor.khabarlagbe.navigation.Screen
import com.noor.khabarlagbe.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleOrderScreen(
    navController: NavController,
    viewModel: ScheduleOrderViewModel = hiltViewModel()
) {
    val scheduledOrders by viewModel.scheduledOrders.collectAsState()
    var showScheduleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.MealPlan.route) }) {
                        Icon(Icons.Filled.CalendarMonth, "Meal Plans")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showScheduleDialog = true },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Schedule, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Schedule New")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Benefits card
            item {
                SchedulingBenefitsCard()
            }

            // Quick schedule options
            item {
                QuickScheduleOptions(
                    onTonightClick = { showScheduleDialog = true },
                    onTomorrowClick = { showScheduleDialog = true },
                    onWeekendClick = { showScheduleDialog = true }
                )
            }

            // Upcoming orders
            if (scheduledOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "Upcoming Scheduled Orders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(scheduledOrders.filter { it.status == ScheduledOrderStatus.SCHEDULED }) { order ->
                    ScheduledOrderCard(
                        order = order,
                        onEdit = { /* Edit order */ },
                        onCancel = { viewModel.cancelScheduledOrder(order.id) }
                    )
                }
            }

            // Empty state
            if (scheduledOrders.none { it.status == ScheduledOrderStatus.SCHEDULED }) {
                item {
                    EmptyScheduleState()
                }
            }
        }
    }

    // Schedule dialog
    if (showScheduleDialog) {
        ScheduleOrderDialog(
            onDismiss = { showScheduleDialog = false },
            onSchedule = { restaurantName, time, isRecurring ->
                // Simplified scheduling
                showScheduleDialog = false
            }
        )
    }
}

@Composable
private fun SchedulingBenefitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Schedule,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Never Miss a Meal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Schedule orders up to 7 days in advance and never worry about ordering again",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun QuickScheduleOptions(
    onTonightClick: () -> Unit,
    onTomorrowClick: () -> Unit,
    onWeekendClick: () -> Unit
) {
    Column {
        Text(
            text = "Quick Schedule",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickScheduleChip(
                label = "Tonight",
                subtitle = "7:00 PM",
                onClick = onTonightClick,
                modifier = Modifier.weight(1f)
            )
            QuickScheduleChip(
                label = "Tomorrow",
                subtitle = "12:00 PM",
                onClick = onTomorrowClick,
                modifier = Modifier.weight(1f)
            )
            QuickScheduleChip(
                label = "Weekend",
                subtitle = "Saturday",
                onClick = onWeekendClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickScheduleChip(
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun ScheduledOrderCard(
    order: ScheduledOrder,
    onEdit: () -> Unit,
    onCancel: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("EEE, MMM dd 'at' hh:mm a", Locale.getDefault()) }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Schedule,
                            null,
                            tint = Primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dateFormat.format(Date(order.scheduledTime)),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                if (order.isRecurring) {
                    Surface(
                        color = Info.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Repeat,
                                null,
                                tint = Info,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Recurring",
                                style = MaterialTheme.typography.labelSmall,
                                color = Info
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${order.deliveryAddress.area}, ${order.deliveryAddress.district}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onEdit) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
private fun EmptyScheduleState() {
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
                Icons.Filled.EventAvailable,
                null,
                tint = TextDisabled,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No scheduled orders",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                text = "Plan your meals ahead of time",
                style = MaterialTheme.typography.bodySmall,
                color = TextDisabled
            )
        }
    }
}

@Composable
private fun ScheduleOrderDialog(
    onDismiss: () -> Unit,
    onSchedule: (String, Long, Boolean) -> Unit
) {
    var selectedDate by remember { mutableStateOf(Date(System.currentTimeMillis() + 86400000)) }
    var selectedTime by remember { mutableStateOf("19:00") }
    var isRecurring by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Order", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Select date and time for delivery",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Time selection chips
                Text("Delivery Time", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("12:00", "14:00", "19:00", "21:00").forEach { time ->
                        FilterChip(
                            selected = selectedTime == time,
                            onClick = { selectedTime = time },
                            label = { Text(time) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recurring toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Make it recurring", fontWeight = FontWeight.Medium)
                        Text(
                            "Repeat this order weekly",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSchedule("Restaurant", selectedDate.time, isRecurring)
            }) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

package com.noor.khabarlagbe.presentation.social

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupOrderScreen(
    navController: NavController,
    viewModel: GroupOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeGroupOrders by viewModel.activeGroupOrders.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.GroupAdd, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Group")
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
            // Join with code card
            item {
                JoinGroupCard(onClick = { showJoinDialog = true })
            }

            // How it works section
            item {
                HowItWorksCard()
            }

            // Active group orders
            if (activeGroupOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "Active Group Orders",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(activeGroupOrders) { groupOrder ->
                    GroupOrderCard(
                        groupOrder = groupOrder,
                        onClick = { /* Navigate to group order details */ },
                        onShare = { /* Share code */ }
                    )
                }
            }
        }
    }

    // Create Group Dialog
    if (showCreateDialog) {
        CreateGroupOrderDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { restaurantId, restaurantName, deadline ->
                viewModel.createGroupOrder(restaurantId, restaurantName, deadline)
                showCreateDialog = false
            }
        )
    }

    // Join Group Dialog
    if (showJoinDialog) {
        JoinGroupOrderDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { code ->
                viewModel.joinGroupOrder(code)
                showJoinDialog = false
            }
        )
    }
}

@Composable
private fun JoinGroupCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.QrCode, null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Join with Code",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enter a 6-digit code to join a group order",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Icon(Icons.Filled.ChevronRight, null, tint = Primary)
        }
    }
}

@Composable
private fun HowItWorksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "How Group Orders Work",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            HowItWorksStep(
                number = 1,
                title = "Create or Join",
                description = "Start a new group order or join with a code"
            )
            HowItWorksStep(
                number = 2,
                title = "Invite Friends",
                description = "Share the code with friends to add their items"
            )
            HowItWorksStep(
                number = 3,
                title = "Everyone Orders",
                description = "Each person adds their items before the deadline"
            )
            HowItWorksStep(
                number = 4,
                title = "Single Delivery",
                description = "One delivery, split the costs, enjoy together!"
            )
        }
    }
}

@Composable
private fun HowItWorksStep(number: Int, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun GroupOrderCard(
    groupOrder: GroupOrder,
    onClick: () -> Unit,
    onShare: () -> Unit
) {
    val remainingTime = remember(groupOrder.deadline) {
        val diff = groupOrder.deadline - System.currentTimeMillis()
        if (diff > 0) "${diff / 60000} min left" else "Expired"
    }

    Card(
        onClick = onClick,
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
                        text = groupOrder.restaurantName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hosted by ${groupOrder.hostName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                StatusBadge(groupOrder.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Participants avatars
            Row(verticalAlignment = Alignment.CenterVertically) {
                groupOrder.participants.take(3).forEachIndexed { index, participant ->
                    Box(
                        modifier = Modifier
                            .offset(x = (-8 * index).dp)
                            .size(32.dp)
                            .background(
                                listOf(Primary, Secondary, Warning)[index % 3],
                                CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = participant.userName.first().toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
                
                if (groupOrder.participants.size > 3) {
                    Box(
                        modifier = Modifier
                            .offset(x = (-24).dp)
                            .size(32.dp)
                            .background(SurfaceVariant, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${groupOrder.participants.size - 3}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Surface(
                    color = Warning.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            null,
                            tint = Warning,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = remainingTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = Warning
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Share code section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Share Code", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        text = groupOrder.shareCode,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                }
                
                OutlinedButton(onClick = onShare) {
                    Icon(Icons.Filled.Share, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: GroupOrderStatus) {
    val (color, text) = when (status) {
        GroupOrderStatus.OPEN -> Success to "Open"
        GroupOrderStatus.LOCKED -> Warning to "Locked"
        GroupOrderStatus.SUBMITTED -> Info to "Submitted"
        GroupOrderStatus.CONFIRMED -> Primary to "Confirmed"
        GroupOrderStatus.DELIVERED -> Success to "Delivered"
        GroupOrderStatus.CANCELLED -> Error to "Cancelled"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CreateGroupOrderDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Long) -> Unit
) {
    var selectedRestaurant by remember { mutableStateOf("Star Kabab") }
    var deadlineMinutes by remember { mutableIntStateOf(60) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Group Order", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Restaurant", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                // Simplified restaurant selector
                OutlinedTextField(
                    value = selectedRestaurant,
                    onValueChange = { selectedRestaurant = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Restaurant Name") },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Order Deadline", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 60, 120).forEach { minutes ->
                        FilterChip(
                            selected = deadlineMinutes == minutes,
                            onClick = { deadlineMinutes = minutes },
                            label = { Text("${if (minutes < 60) minutes else minutes / 60} ${if (minutes < 60) "min" else "hr"}") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreate(
                        "rest_${System.currentTimeMillis()}",
                        selectedRestaurant,
                        System.currentTimeMillis() + deadlineMinutes * 60 * 1000
                    )
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun JoinGroupOrderDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Join Group Order", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Enter the 6-digit code shared by your friend",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { if (it.length <= 6) code = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Share Code") },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        letterSpacing = 8.sp,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onJoin(code) },
                enabled = code.length == 6
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

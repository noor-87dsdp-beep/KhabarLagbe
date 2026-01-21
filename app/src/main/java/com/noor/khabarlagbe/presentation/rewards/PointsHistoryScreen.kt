package com.noor.khabarlagbe.presentation.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.PointsTransaction
import com.noor.khabarlagbe.domain.model.PointsTransactionType
import com.noor.khabarlagbe.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsHistoryScreen(
    navController: NavController,
    viewModel: RewardsViewModel = hiltViewModel()
) {
    val pointsHistory by viewModel.pointsHistory.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val currentPoints = when (val state = uiState) {
        is RewardsUiState.Success -> state.loyaltyProfile.currentPoints
        else -> 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Points History", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Balance summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$currentPoints",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "points",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Transaction list
            items(pointsHistory) { transaction ->
                PointsTransactionItem(transaction)
            }

            if (pointsHistory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.History,
                                null,
                                tint = TextDisabled,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions yet", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PointsTransactionItem(transaction: PointsTransaction) {
    val isPositive = transaction.points > 0
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = getTransactionColor(transaction.type).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTransactionIcon(transaction.type),
                    contentDescription = null,
                    tint = getTransactionColor(transaction.type),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(Date(transaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Points
            Text(
                text = "${if (isPositive) "+" else ""}${transaction.points}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) Success else Error
            )
        }
    }
}

private fun getTransactionIcon(type: PointsTransactionType): ImageVector = when (type) {
    PointsTransactionType.EARNED_ORDER -> Icons.Filled.ShoppingBag
    PointsTransactionType.EARNED_REFERRAL -> Icons.Filled.PersonAdd
    PointsTransactionType.EARNED_BONUS -> Icons.Filled.CardGiftcard
    PointsTransactionType.EARNED_REVIEW -> Icons.Filled.RateReview
    PointsTransactionType.REDEEMED -> Icons.Filled.Redeem
    PointsTransactionType.EXPIRED -> Icons.Filled.Schedule
    PointsTransactionType.ADJUSTED -> Icons.Filled.Tune
}

private fun getTransactionColor(type: PointsTransactionType): Color = when (type) {
    PointsTransactionType.EARNED_ORDER -> Primary
    PointsTransactionType.EARNED_REFERRAL -> Info
    PointsTransactionType.EARNED_BONUS -> Warning
    PointsTransactionType.EARNED_REVIEW -> Success
    PointsTransactionType.REDEEMED -> DeliveryBadge
    PointsTransactionType.EXPIRED -> Error
    PointsTransactionType.ADJUSTED -> TextSecondary
}

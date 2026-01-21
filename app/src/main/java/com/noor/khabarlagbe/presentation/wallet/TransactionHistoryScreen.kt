package com.noor.khabarlagbe.presentation.wallet

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
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.ui.theme.*
import com.noor.khabarlagbe.util.Constants
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    var selectedFilter by remember { mutableStateOf<WalletTransactionType?>(null) }

    val filteredTransactions = if (selectedFilter == null) {
        transactions
    } else {
        transactions.filter { it.type == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            FilterChipsRow(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            if (filteredTransactions.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Group by date
                    val groupedTransactions = filteredTransactions.groupBy { transaction ->
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(Date(transaction.timestamp))
                    }

                    groupedTransactions.forEach { (date, dayTransactions) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(dayTransactions) { transaction ->
                            DetailedTransactionItem(transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: WalletTransactionType?,
    onFilterSelected: (WalletTransactionType?) -> Unit
) {
    val filters = listOf(
        null to "All",
        WalletTransactionType.TOP_UP to "Top Up",
        WalletTransactionType.PAYMENT to "Payments",
        WalletTransactionType.CASHBACK to "Cashback",
        WalletTransactionType.REFUND to "Refunds"
    )

    ScrollableTabRow(
        selectedTabIndex = filters.indexOfFirst { it.first == selectedFilter }.coerceAtLeast(0),
        edgePadding = 16.dp,
        containerColor = Color.Transparent,
        divider = { }
    ) {
        filters.forEach { (type, label) ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterSelected(type) },
                label = { Text(label) },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun DetailedTransactionItem(transaction: WalletTransaction) {
    val isPositive = transaction.amount > 0
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

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
            Box(
                modifier = Modifier
                    .size(48.dp)
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeFormat.format(Date(transaction.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    if (transaction.referenceId != null) {
                        Text(" • ", color = TextSecondary)
                        Text(
                            text = transaction.referenceId!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isPositive) "+" else ""}${Constants.CURRENCY_SYMBOL}${String.format("%.2f", kotlin.math.abs(transaction.amount))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) Success else Error
                )
                StatusBadge(transaction.status)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: WalletTransactionStatus) {
    val (color, text) = when (status) {
        WalletTransactionStatus.COMPLETED -> Success to "Completed"
        WalletTransactionStatus.PENDING -> Warning to "Pending"
        WalletTransactionStatus.FAILED -> Error to "Failed"
        WalletTransactionStatus.CANCELLED -> TextSecondary to "Cancelled"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.ReceiptLong,
            null,
            tint = TextDisabled,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No transactions found",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Text(
            text = "Your transactions will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDisabled
        )
    }
}

private fun getTransactionIcon(type: WalletTransactionType): ImageVector = when (type) {
    WalletTransactionType.TOP_UP -> Icons.Filled.Add
    WalletTransactionType.PAYMENT -> Icons.Filled.ShoppingBag
    WalletTransactionType.REFUND -> Icons.Filled.Replay
    WalletTransactionType.CASHBACK -> Icons.Filled.Savings
    WalletTransactionType.REWARD_REDEMPTION -> Icons.Filled.CardGiftcard
    WalletTransactionType.REFERRAL_BONUS -> Icons.Filled.PersonAdd
    WalletTransactionType.PROMOTIONAL_CREDIT -> Icons.Filled.LocalOffer
}

private fun getTransactionColor(type: WalletTransactionType): Color = when (type) {
    WalletTransactionType.TOP_UP -> Success
    WalletTransactionType.PAYMENT -> Primary
    WalletTransactionType.REFUND -> Info
    WalletTransactionType.CASHBACK -> Warning
    WalletTransactionType.REWARD_REDEMPTION -> DeliveryBadge
    WalletTransactionType.REFERRAL_BONUS -> Info
    WalletTransactionType.PROMOTIONAL_CREDIT -> Success
}

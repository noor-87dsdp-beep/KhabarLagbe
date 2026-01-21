package com.noor.khabarlagbe.presentation.wallet

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.navigation.Screen
import com.noor.khabarlagbe.presentation.components.ShimmerEffect
import com.noor.khabarlagbe.ui.theme.*
import com.noor.khabarlagbe.util.Constants
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.TransactionHistory.route) }) {
                        Icon(Icons.Filled.Receipt, "Transaction History")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is WalletUiState.Loading -> WalletLoadingState(Modifier.padding(padding))
            is WalletUiState.Success -> WalletContent(
                wallet = state.wallet,
                transactions = transactions.take(5),
                onAddMoneyClick = { navController.navigate(Screen.AddMoney.route) },
                onViewAllTransactions = { navController.navigate(Screen.TransactionHistory.route) },
                modifier = Modifier.padding(padding)
            )
            is WalletUiState.Error -> ErrorContent(state.message) { viewModel.loadWalletData() }
        }
    }
}

@Composable
private fun WalletContent(
    wallet: Wallet,
    transactions: List<WalletTransaction>,
    onAddMoneyClick: () -> Unit,
    onViewAllTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card
        item {
            WalletBalanceCard(
                balance = wallet.balance,
                currency = wallet.currency,
                onAddMoneyClick = onAddMoneyClick
            )
        }

        // Quick Actions
        item {
            QuickActionsRow(
                onAddMoneyClick = onAddMoneyClick,
                onSendMoneyClick = { },
                onPayBillsClick = { }
            )
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllTransactions) {
                    Text("See All", color = Primary)
                }
            }
        }

        // Transaction list
        if (transactions.isEmpty()) {
            item {
                EmptyTransactionsCard()
            }
        } else {
            items(transactions) { transaction ->
                WalletTransactionItem(transaction)
            }
        }
    }
}

@Composable
private fun WalletBalanceCard(
    balance: Double,
    currency: String,
    onAddMoneyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, PrimaryVariant)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = Constants.CURRENCY_SYMBOL,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = String.format("%.2f", balance),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.AccountBalanceWallet,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAddMoneyClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Money", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAddMoneyClick: () -> Unit,
    onSendMoneyClick: () -> Unit,
    onPayBillsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionItem(
            icon = Icons.Filled.Add,
            label = "Add Money",
            onClick = onAddMoneyClick
        )
        QuickActionItem(
            icon = Icons.Filled.Send,
            label = "Send",
            onClick = onSendMoneyClick
        )
        QuickActionItem(
            icon = Icons.Filled.Receipt,
            label = "Pay Bills",
            onClick = onPayBillsClick
        )
        QuickActionItem(
            icon = Icons.Filled.QrCodeScanner,
            label = "Scan QR",
            onClick = { }
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Primary, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun WalletTransactionItem(transaction: WalletTransaction) {
    val isPositive = transaction.amount > 0
    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }

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
                    .size(44.dp)
                    .background(
                        color = getTransactionTypeColor(transaction.type).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTransactionTypeIcon(transaction.type),
                    contentDescription = null,
                    tint = getTransactionTypeColor(transaction.type),
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
                Text(
                    text = dateFormat.format(Date(transaction.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isPositive) "+" else ""}${Constants.CURRENCY_SYMBOL}${String.format("%.2f", kotlin.math.abs(transaction.amount))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) Success else Error
                )
                Surface(
                    color = when (transaction.status) {
                        WalletTransactionStatus.COMPLETED -> Success.copy(alpha = 0.1f)
                        WalletTransactionStatus.PENDING -> Warning.copy(alpha = 0.1f)
                        else -> Error.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = transaction.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (transaction.status) {
                            WalletTransactionStatus.COMPLETED -> Success
                            WalletTransactionStatus.PENDING -> Warning
                            else -> Error
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTransactionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.ReceiptLong,
                null,
                tint = TextDisabled,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("No transactions yet", color = TextSecondary)
        }
    }
}

@Composable
private fun WalletLoadingState(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Error, null, tint = Error, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

private fun getTransactionTypeIcon(type: WalletTransactionType): ImageVector = when (type) {
    WalletTransactionType.TOP_UP -> Icons.Filled.Add
    WalletTransactionType.PAYMENT -> Icons.Filled.ShoppingBag
    WalletTransactionType.REFUND -> Icons.Filled.Replay
    WalletTransactionType.CASHBACK -> Icons.Filled.Savings
    WalletTransactionType.REWARD_REDEMPTION -> Icons.Filled.CardGiftcard
    WalletTransactionType.REFERRAL_BONUS -> Icons.Filled.PersonAdd
    WalletTransactionType.PROMOTIONAL_CREDIT -> Icons.Filled.LocalOffer
}

private fun getTransactionTypeColor(type: WalletTransactionType): Color = when (type) {
    WalletTransactionType.TOP_UP -> Success
    WalletTransactionType.PAYMENT -> Primary
    WalletTransactionType.REFUND -> Info
    WalletTransactionType.CASHBACK -> Warning
    WalletTransactionType.REWARD_REDEMPTION -> DeliveryBadge
    WalletTransactionType.REFERRAL_BONUS -> Info
    WalletTransactionType.PROMOTIONAL_CREDIT -> Success
}

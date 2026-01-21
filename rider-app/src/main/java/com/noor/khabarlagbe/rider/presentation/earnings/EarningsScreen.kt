package com.noor.khabarlagbe.rider.presentation.earnings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.rider.presentation.components.EarningsCard
import com.noor.khabarlagbe.rider.presentation.components.EarningsBreakdownCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(
    navController: NavController,
    viewModel: EarningsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val withdrawalState by viewModel.withdrawalState.collectAsState()
    
    var showWithdrawDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(withdrawalState) {
        when (withdrawalState) {
            is WithdrawalUiState.Success -> {
                snackbarHostState.showSnackbar("উত্তোলন অনুরোধ সফল হয়েছে")
                viewModel.resetWithdrawalState()
            }
            is WithdrawalUiState.Error -> {
                snackbarHostState.showSnackbar((withdrawalState as WithdrawalUiState.Error).message)
                viewModel.resetWithdrawalState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("আয়") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (uiState) {
            is EarningsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is EarningsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text((uiState as EarningsUiState.Error).message)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadEarnings() }) { Text("আবার চেষ্টা করুন") }
                }
            }
            
            is EarningsUiState.Success -> {
                val successState = uiState as EarningsUiState.Success
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Period filter
                    item {
                        PeriodFilterRow(
                            selectedPeriod = selectedPeriod,
                            onPeriodSelected = { viewModel.selectPeriod(it) }
                        )
                    }
                    
                    // Main earnings card
                    item {
                        val summary = viewModel.getSelectedSummary()
                        if (summary != null) {
                            EarningsCard(
                                summary = summary,
                                title = when (selectedPeriod) {
                                    EarningsPeriod.TODAY -> "আজকের আয়"
                                    EarningsPeriod.THIS_WEEK -> "এই সপ্তাহের আয়"
                                    EarningsPeriod.THIS_MONTH -> "এই মাসের আয়"
                                    EarningsPeriod.CUSTOM -> "নির্বাচিত সময়ের আয়"
                                },
                                onWithdraw = { showWithdrawDialog = true }
                            )
                        }
                    }
                    
                    // Available balance
                    item {
                        AvailableBalanceCard(
                            balance = successState.availableBalance,
                            onWithdraw = { showWithdrawDialog = true }
                        )
                    }
                    
                    // Breakdown
                    item {
                        val summary = viewModel.getSelectedSummary()
                        if (summary != null) {
                            EarningsBreakdownCard(summary = summary)
                        }
                    }
                    
                    // Daily chart placeholder
                    if (successState.dailyBreakdown.isNotEmpty()) {
                        item {
                            DailyEarningsChart(dailyBreakdown = successState.dailyBreakdown)
                        }
                    }
                    
                    // Withdrawal history
                    if (successState.withdrawalHistory.isNotEmpty()) {
                        item {
                            Text(
                                text = "উত্তোলন ইতিহাস",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(successState.withdrawalHistory) { withdrawal ->
                            WithdrawalHistoryItem(withdrawal = withdrawal)
                        }
                    }
                }
            }
        }
    }
    
    // Withdraw dialog
    if (showWithdrawDialog) {
        WithdrawDialog(
            availableBalance = (uiState as? EarningsUiState.Success)?.availableBalance ?: 0.0,
            isLoading = withdrawalState is WithdrawalUiState.Loading,
            onWithdraw = { amount, method ->
                viewModel.requestWithdrawal(amount, method, null)
            },
            onDismiss = { showWithdrawDialog = false }
        )
    }
}

@Composable
private fun PeriodFilterRow(
    selectedPeriod: EarningsPeriod,
    onPeriodSelected: (EarningsPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EarningsPeriod.values().filter { it != EarningsPeriod.CUSTOM }.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        text = when (period) {
                            EarningsPeriod.TODAY -> "আজ"
                            EarningsPeriod.THIS_WEEK -> "এই সপ্তাহ"
                            EarningsPeriod.THIS_MONTH -> "এই মাস"
                            EarningsPeriod.CUSTOM -> "কাস্টম"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun AvailableBalanceCard(
    balance: Double,
    onWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "উত্তোলনযোগ্য ব্যালেন্স",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "৳${String.format("%,.0f", balance)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Button(
                onClick = onWithdraw,
                enabled = balance > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Icon(Icons.Default.AccountBalanceWallet, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("উত্তোলন")
            }
        }
    }
}

@Composable
private fun DailyEarningsChart(
    dailyBreakdown: List<com.noor.khabarlagbe.rider.data.dto.DailyEarningsDto>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "দৈনিক আয়",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple bar chart representation
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxEarnings = dailyBreakdown.maxOfOrNull { it.earnings } ?: 1.0
                
                dailyBreakdown.takeLast(7).forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((80 * (day.earnings / maxEarnings)).dp)
                                .padding(bottom = 4.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                                color = Color(0xFF4CAF50)
                            ) {}
                        }
                        Text(
                            text = day.date.takeLast(2),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WithdrawalHistoryItem(
    withdrawal: com.noor.khabarlagbe.rider.data.dto.WithdrawalDto
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "৳${String.format("%,.0f", withdrawal.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = withdrawal.method,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (withdrawal.status.lowercase()) {
                    "completed" -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    "pending" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                    else -> Color.Red.copy(alpha = 0.1f)
                }
            ) {
                Text(
                    text = when (withdrawal.status.lowercase()) {
                        "completed" -> "সম্পন্ন"
                        "pending" -> "প্রক্রিয়াধীন"
                        else -> "ব্যর্থ"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = when (withdrawal.status.lowercase()) {
                        "completed" -> Color(0xFF4CAF50)
                        "pending" -> Color(0xFFFF9800)
                        else -> Color.Red
                    }
                )
            }
        }
    }
}

@Composable
private fun WithdrawDialog(
    availableBalance: Double,
    isLoading: Boolean,
    onWithdraw: (Double, String) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("bkash") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("উত্তোলন") },
        text = {
            Column {
                Text("উত্তোলনযোগ্য: ৳${String.format("%,.0f", availableBalance)}")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() } },
                    label = { Text("পরিমাণ (৳)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("পেমেন্ট পদ্ধতি", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedMethod == "bkash",
                        onClick = { selectedMethod = "bkash" },
                        label = { Text("বিকাশ") }
                    )
                    FilterChip(
                        selected = selectedMethod == "nagad",
                        onClick = { selectedMethod = "nagad" },
                        label = { Text("নগদ") }
                    )
                    FilterChip(
                        selected = selectedMethod == "bank",
                        onClick = { selectedMethod = "bank" },
                        label = { Text("ব্যাংক") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (amountValue > 0 && amountValue <= availableBalance) {
                        onWithdraw(amountValue, selectedMethod)
                        onDismiss()
                    }
                },
                enabled = !isLoading && amount.isNotBlank() && 
                         (amount.toDoubleOrNull() ?: 0.0) > 0 &&
                         (amount.toDoubleOrNull() ?: 0.0) <= availableBalance
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text("উত্তোলন করুন")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}

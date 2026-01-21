package com.noor.khabarlagbe.presentation.wallet

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.TopUpOption
import com.noor.khabarlagbe.ui.theme.*
import com.noor.khabarlagbe.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoneyScreen(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val topUpOptions by viewModel.topUpOptions.collectAsState()
    
    var selectedAmount by remember { mutableDoubleStateOf(0.0) }
    var customAmount by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("bKash") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is WalletUiState.Success && (uiState as WalletUiState.Success).topUpSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Money", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Amount Selection
            Text(
                text = "Select Amount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(topUpOptions) { option ->
                    TopUpOptionCard(
                        option = option,
                        isSelected = selectedAmount == option.amount,
                        onClick = {
                            selectedAmount = option.amount
                            customAmount = ""
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom amount
            OutlinedTextField(
                value = customAmount,
                onValueChange = {
                    customAmount = it
                    selectedAmount = it.toDoubleOrNull() ?: 0.0
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Or enter custom amount") },
                leadingIcon = { Text(Constants.CURRENCY_SYMBOL, fontWeight = FontWeight.Bold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Method Selection
            Text(
                text = "Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodOption(
                name = "bKash",
                description = "Pay with bKash",
                isSelected = selectedPaymentMethod == "bKash",
                onClick = { selectedPaymentMethod = "bKash" }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PaymentMethodOption(
                name = "Nagad",
                description = "Pay with Nagad",
                isSelected = selectedPaymentMethod == "Nagad",
                onClick = { selectedPaymentMethod = "Nagad" }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PaymentMethodOption(
                name = "Card",
                description = "Credit/Debit Card",
                isSelected = selectedPaymentMethod == "Card",
                onClick = { selectedPaymentMethod = "Card" }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Summary and Add button
            if (selectedAmount > 0) {
                val bonus = topUpOptions.find { it.amount == selectedAmount }?.bonusAmount ?: 0.0
                val total = selectedAmount + bonus

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Amount", color = TextSecondary)
                            Text("${Constants.CURRENCY_SYMBOL}${String.format("%.2f", selectedAmount)}")
                        }
                        if (bonus > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bonus", color = Success)
                                Text(
                                    "+${Constants.CURRENCY_SYMBOL}${String.format("%.2f", bonus)}",
                                    color = Success,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Credit", fontWeight = FontWeight.Bold)
                            Text(
                                "${Constants.CURRENCY_SYMBOL}${String.format("%.2f", total)}",
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isProcessing = (uiState as? WalletUiState.Success)?.isProcessing ?: false
            
            Button(
                onClick = { viewModel.addMoney(selectedAmount, selectedPaymentMethod) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAmount >= Constants.MIN_TOP_UP_AMOUNT && !isProcessing,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (selectedAmount > 0) "Add ${Constants.CURRENCY_SYMBOL}${selectedAmount.toInt()}" else "Select an amount",
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (selectedAmount > 0 && selectedAmount < Constants.MIN_TOP_UP_AMOUNT) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Minimum amount is ${Constants.CURRENCY_SYMBOL}${Constants.MIN_TOP_UP_AMOUNT.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.clearTopUpSuccess()
                navController.popBackStack()
            },
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    null,
                    tint = Success,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Money Added!", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Successfully added ${Constants.CURRENCY_SYMBOL}${selectedAmount.toInt()} to your wallet",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.clearTopUpSuccess()
                        navController.popBackStack()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun TopUpOptionCard(
    option: TopUpOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .scale(scale)
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary else SurfaceLight
        ),
        border = if (option.isPopular && !isSelected) BorderStroke(2.dp, Primary) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (option.isPopular) {
                    Surface(
                        color = if (isSelected) Color.White.copy(alpha = 0.2f) else Primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Popular",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                Text(
                    text = "${Constants.CURRENCY_SYMBOL}${option.amount.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextPrimary
                )
                
                if (option.bonusAmount > 0) {
                    Text(
                        text = "+${Constants.CURRENCY_SYMBOL}${option.bonusAmount.toInt()} bonus",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else Success
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    name: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.1f) else SurfaceLight
        ),
        border = if (isSelected) BorderStroke(2.dp, Primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = Primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

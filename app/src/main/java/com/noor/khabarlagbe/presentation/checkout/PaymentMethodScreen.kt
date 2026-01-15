package com.noor.khabarlagbe.presentation.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.PaymentMethod
import com.noor.khabarlagbe.presentation.components.PrimaryButton
import com.noor.khabarlagbe.ui.theme.Primary

/**
 * Payment Method Selection Screen
 * 
 * Features:
 * - Payment options: bKash, Nagad, Rocket, Card, Cash on Delivery
 * - Radio button selection
 * - Payment logos (Material Icons placeholders)
 * - Continue button
 * - Save preference checkbox
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    navController: NavController,
    onPaymentMethodSelected: (PaymentMethod, Boolean) -> Unit = { _, _ -> }
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var savePreference by remember { mutableStateOf(false) }

    // Payment methods displayed in the app
    // Note: Not all PaymentMethod enum values are shown in the UI
    // UPAY and legacy card options are excluded for better UX:
    // - UPAY has lower market adoption compared to bKash/Nagad/Rocket
    // - CREDIT_CARD/DEBIT_CARD are consolidated under SSL_COMMERZ gateway
    // The enum maintains all values for backend compatibility and future expansion
    val paymentMethods = listOf(
        PaymentMethodItem(
            method = PaymentMethod.BKASH,
            name = "bKash",
            description = "Pay with bKash mobile wallet",
            icon = Icons.Filled.AccountBalance,
            iconColor = Color(0xFFE2136E)
        ),
        PaymentMethodItem(
            method = PaymentMethod.NAGAD,
            name = "Nagad",
            description = "Pay with Nagad mobile wallet",
            icon = Icons.Filled.AccountBalance,
            iconColor = Color(0xFFEC1C24)
        ),
        PaymentMethodItem(
            method = PaymentMethod.ROCKET,
            name = "Rocket",
            description = "Pay with Rocket mobile banking",
            icon = Icons.Filled.AccountBalance,
            iconColor = Color(0xFF8A2BE2)
        ),
        PaymentMethodItem(
            method = PaymentMethod.SSL_COMMERZ,
            name = "Credit/Debit Card",
            description = "Pay with Visa, Mastercard, or other cards",
            icon = Icons.Filled.CreditCard,
            iconColor = Primary
        ),
        PaymentMethodItem(
            method = PaymentMethod.CASH_ON_DELIVERY,
            name = "Cash on Delivery",
            description = "Pay with cash when order arrives",
            icon = Icons.Filled.Money,
            iconColor = Color(0xFF4CAF50)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Method") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Select Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(paymentMethods) { paymentMethodItem ->
                    PaymentMethodCard(
                        paymentMethodItem = paymentMethodItem,
                        isSelected = selectedMethod == paymentMethodItem.method,
                        onSelect = { selectedMethod = paymentMethodItem.method }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Save preference checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = savePreference,
                            onCheckedChange = { savePreference = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save this as my preferred payment method",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Payment info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Secure Payment",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your payment information is encrypted and secure",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Continue Button
            Surface(
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        text = "Continue",
                        onClick = {
                            selectedMethod?.let { method ->
                                onPaymentMethodSelected(method, savePreference)
                                navController.navigateUp()
                            }
                        },
                        enabled = selectedMethod != null
                    )
                }
            }
        }
    }
}

/**
 * Payment method card with radio button
 */
@Composable
private fun PaymentMethodCard(
    paymentMethodItem: PaymentMethodItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Payment Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = paymentMethodItem.iconColor.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = paymentMethodItem.icon,
                        contentDescription = null,
                        tint = paymentMethodItem.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Payment Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = paymentMethodItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = paymentMethodItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Radio Button
            RadioButton(
                selected = isSelected,
                onClick = null
            )
        }
    }
}

/**
 * Data class for payment method item
 */
private data class PaymentMethodItem(
    val method: PaymentMethod,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color
)

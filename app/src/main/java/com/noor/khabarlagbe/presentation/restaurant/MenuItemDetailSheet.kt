package com.noor.khabarlagbe.presentation.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noor.khabarlagbe.domain.model.MenuItem
import com.noor.khabarlagbe.domain.model.CustomizationType
import com.noor.khabarlagbe.domain.model.CustomizationChoice
import com.noor.khabarlagbe.presentation.components.QuantitySelector
import com.noor.khabarlagbe.presentation.components.PrimaryButton
import com.noor.khabarlagbe.ui.theme.Primary
import com.noor.khabarlagbe.util.Constants

/**
 * Menu Item Detail Bottom Sheet
 * 
 * Features:
 * - Modal bottom sheet with menu item details
 * - Size selection with RadioButtons (Small/Medium/Large)
 * - Add-ons section with Checkboxes and prices
 * - Spice level selector
 * - Special instructions text field
 * - Quantity selector
 * - Real-time price calculation
 * - Add to Cart button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDetailSheet(
    menuItem: MenuItem,
    onDismiss: () -> Unit,
    onAddToCart: (
        item: MenuItem,
        quantity: Int,
        selectedCustomizations: Map<String, Set<String>>,
        specialInstructions: String?
    ) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    // Store selected customization choices by option ID
    var selectedCustomizations by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var specialInstructions by remember { mutableStateOf("") }

    // Fallback customization data for UI demo when menuItem.customizations is empty
    // In production, this will use actual data from menuItem.customizations
    val hasCustomizations = menuItem.customizations.isNotEmpty()
    
    // Price calculations based on actual customization data
    val customizationsPrice = if (hasCustomizations) {
        menuItem.customizations.sumOf { option ->
            val selectedChoiceIds = selectedCustomizations[option.id] ?: emptySet()
            option.options.filter { it.id in selectedChoiceIds }
                .sumOf { it.additionalPrice }
        }
    } else {
        // Fallback pricing for demo when backend data isn't available
        0.0
    }

    val totalItemPrice = menuItem.price + customizationsPrice
    val totalPrice = totalItemPrice * quantity

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Customize your order",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Item Image
            if (!menuItem.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = menuItem.imageUrl,
                    contentDescription = menuItem.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Item Name and Description
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${Constants.CURRENCY_SYMBOL}${menuItem.price}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Customization Options from MenuItem data
            if (hasCustomizations) {
                menuItem.customizations.forEach { option ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (option.isRequired) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "*",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        when (option.type) {
                            CustomizationType.SINGLE_SELECT -> {
                                // Radio buttons for single select
                                Column(modifier = Modifier.selectableGroup()) {
                                    option.options.forEach { choice ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .selectable(
                                                    selected = selectedCustomizations[option.id]?.contains(choice.id) == true,
                                                    onClick = {
                                                        selectedCustomizations = selectedCustomizations.toMutableMap().apply {
                                                            this[option.id] = setOf(choice.id)
                                                        }
                                                    },
                                                    role = Role.RadioButton
                                                )
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = selectedCustomizations[option.id]?.contains(choice.id) == true,
                                                onClick = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = choice.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (choice.additionalPrice > 0) {
                                                Text(
                                                    text = "+${Constants.CURRENCY_SYMBOL}${choice.additionalPrice}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            CustomizationType.MULTIPLE_SELECT -> {
                                // Checkboxes for multiple select
                                option.options.forEach { choice ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedCustomizations[option.id]?.contains(choice.id) == true,
                                            onCheckedChange = { checked ->
                                                selectedCustomizations = selectedCustomizations.toMutableMap().apply {
                                                    val current = this[option.id] ?: emptySet()
                                                    this[option.id] = if (checked) {
                                                        current + choice.id
                                                    } else {
                                                        current - choice.id
                                                    }
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = choice.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (choice.additionalPrice > 0) {
                                            Text(
                                                text = "+${Constants.CURRENCY_SYMBOL}${choice.additionalPrice}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                // Fallback UI for demo purposes when backend data isn't available
                // Size Selection
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Size (Demo - will use backend data)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Connect to backend to see actual customization options",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Special Instructions
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Special Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = specialInstructions,
                    onValueChange = { specialInstructions = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Add any special requests...") },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity and Price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QuantitySelector(
                        quantity = quantity,
                        onQuantityChange = { quantity = it }
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${Constants.CURRENCY_SYMBOL}${"%.2f".format(totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add to Cart Button
            PrimaryButton(
                text = "Add to Cart",
                onClick = {
                    onAddToCart(
                        menuItem,
                        quantity,
                        selectedCustomizations,
                        specialInstructions.ifBlank { null }
                    )
                    onDismiss()
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

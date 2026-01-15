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
        size: String?,
        addOns: List<String>,
        spiceLevel: String?,
        specialInstructions: String?
    ) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedAddOns by remember { mutableStateOf(setOf<String>()) }
    var selectedSpiceLevel by remember { mutableStateOf<String?>(null) }
    var specialInstructions by remember { mutableStateOf("") }

    // Price calculations
    val sizePrice = when (selectedSize) {
        "Small" -> 0.0
        "Medium" -> 50.0
        "Large" -> 100.0
        else -> 0.0
    }

    val addOnsPrice = selectedAddOns.sumOf { addOn ->
        when (addOn) {
            "Extra Cheese" -> 30.0
            "Extra Sauce" -> 20.0
            "Extra Vegetables" -> 40.0
            "Extra Meat" -> 80.0
            else -> 0.0
        }
    }

    val totalItemPrice = menuItem.price + sizePrice + addOnsPrice
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

            // Size Selection
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Size",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.selectableGroup()) {
                    listOf(
                        "Small" to 0.0,
                        "Medium" to 50.0,
                        "Large" to 100.0
                    ).forEach { (size, price) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedSize == size,
                                    onClick = { selectedSize = size },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSize == size,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = size,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (price > 0) "+${Constants.CURRENCY_SYMBOL}$price" else "Base price",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Add-ons Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Add-ons",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    "Extra Cheese" to 30.0,
                    "Extra Sauce" to 20.0,
                    "Extra Vegetables" to 40.0,
                    "Extra Meat" to 80.0
                ).forEach { (addOn, price) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedAddOns.contains(addOn),
                            onCheckedChange = { checked ->
                                selectedAddOns = if (checked) {
                                    selectedAddOns + addOn
                                } else {
                                    selectedAddOns - addOn
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = addOn,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "+${Constants.CURRENCY_SYMBOL}$price",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Spice Level
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Spice Level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.selectableGroup()) {
                    listOf("Mild", "Medium", "Spicy", "Extra Spicy").forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedSpiceLevel == level,
                                    onClick = { selectedSpiceLevel = level },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSpiceLevel == level,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = level,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

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
                        selectedSize,
                        selectedAddOns.toList(),
                        selectedSpiceLevel,
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

package com.noor.khabarlagbe.presentation.profile.addresses

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.Address
import com.noor.khabarlagbe.presentation.components.CustomTextField
import com.noor.khabarlagbe.presentation.components.PrimaryButton
import com.noor.khabarlagbe.ui.theme.Primary
import com.noor.khabarlagbe.util.Constants

/**
 * Add Address Screen
 * 
 * Features:
 * - Map placeholder (Mapbox not configured)
 * - Address form fields: Label, Street, Building, Floor, Instructions
 * - Label selection chips (Home/Work/Other)
 * - "Set as default" checkbox
 * - Save button
 * - Form validation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    navController: NavController,
    onAddressSaved: (Address) -> Unit = {}
) {
    var selectedLabel by remember { mutableStateOf("Home") }
    var houseNo by remember { mutableStateOf("") }
    var roadNo by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var deliveryInstructions by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Validation errors
    var houseNoError by remember { mutableStateOf<String?>(null) }
    var roadNoError by remember { mutableStateOf<String?>(null) }
    var areaError by remember { mutableStateOf<String?>(null) }
    var thanaError by remember { mutableStateOf<String?>(null) }
    var districtError by remember { mutableStateOf<String?>(null) }
    var divisionError by remember { mutableStateOf<String?>(null) }
    var postalCodeError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        var isValid = true

        if (houseNo.isBlank()) {
            houseNoError = "House number is required"
            isValid = false
        } else {
            houseNoError = null
        }

        if (roadNo.isBlank()) {
            roadNoError = "Road number is required"
            isValid = false
        } else {
            roadNoError = null
        }

        if (area.isBlank()) {
            areaError = "Area is required"
            isValid = false
        } else {
            areaError = null
        }

        if (thana.isBlank()) {
            thanaError = "Thana is required"
            isValid = false
        } else {
            thanaError = null
        }

        if (district.isBlank()) {
            districtError = "District is required"
            isValid = false
        } else {
            districtError = null
        }

        if (division.isBlank()) {
            divisionError = "Division is required"
            isValid = false
        } else {
            divisionError = null
        }

        if (postalCode.isBlank()) {
            postalCodeError = "Postal code is required"
            isValid = false
        } else if (postalCode.length != 4 || !postalCode.all { it.isDigit() }) {
            postalCodeError = "Invalid postal code (4 digits)"
            isValid = false
        } else {
            postalCodeError = null
        }

        return isValid
    }

    fun saveAddress() {
        if (!validateForm()) return

        isSaving = true

        try {
            // Create address object
            val address = Address(
                id = System.currentTimeMillis().toString(),
                label = selectedLabel,
                houseNo = houseNo,
                roadNo = roadNo,
                area = area,
                thana = thana,
                district = district,
                division = division,
                postalCode = postalCode,
                landmark = landmark.ifBlank { null },
                deliveryInstructions = deliveryInstructions.ifBlank { null },
                latitude = 23.8103, // Default coordinates (Dhaka)
                longitude = 90.4125,
                isDefault = isDefault
            )

            onAddressSaved(address)
            navController.navigateUp()
        } catch (e: Exception) {
            // Handle any errors during address creation
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Address") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Map will be shown here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "(Mapbox integration pending)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Label Selection
                Text(
                    text = "Address Label *",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Home", "Work", "Other").forEach { label ->
                        FilterChip(
                            selected = selectedLabel == label,
                            onClick = { selectedLabel = label },
                            label = { Text(label) },
                            leadingIcon = if (selectedLabel == label) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }

                Divider()

                // Address Fields
                Text(
                    text = "Address Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = houseNo,
                        onValueChange = {
                            houseNo = it
                            houseNoError = null
                        },
                        label = "House/Flat No. *",
                        placeholder = "e.g., 42",
                        isError = houseNoError != null,
                        errorMessage = houseNoError,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )

                    CustomTextField(
                        value = roadNo,
                        onValueChange = {
                            roadNo = it
                            roadNoError = null
                        },
                        label = "Road No. *",
                        placeholder = "e.g., 12A",
                        isError = roadNoError != null,
                        errorMessage = roadNoError,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )
                }

                CustomTextField(
                    value = area,
                    onValueChange = {
                        area = it
                        areaError = null
                    },
                    label = "Area *",
                    placeholder = "e.g., Dhanmondi, Gulshan",
                    leadingIcon = Icons.Filled.LocationOn,
                    isError = areaError != null,
                    errorMessage = areaError,
                    imeAction = ImeAction.Next
                )

                CustomTextField(
                    value = thana,
                    onValueChange = {
                        thana = it
                        thanaError = null
                    },
                    label = "Thana *",
                    placeholder = "e.g., Dhanmondi, Uttara",
                    isError = thanaError != null,
                    errorMessage = thanaError,
                    imeAction = ImeAction.Next
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = district,
                        onValueChange = {
                            district = it
                            districtError = null
                        },
                        label = "District *",
                        placeholder = "e.g., Dhaka",
                        isError = districtError != null,
                        errorMessage = districtError,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )

                    CustomTextField(
                        value = division,
                        onValueChange = {
                            division = it
                            divisionError = null
                        },
                        label = "Division *",
                        placeholder = "e.g., Dhaka",
                        isError = divisionError != null,
                        errorMessage = divisionError,
                        modifier = Modifier.weight(1f),
                        imeAction = ImeAction.Next
                    )
                }

                CustomTextField(
                    value = postalCode,
                    onValueChange = {
                        if (it.length <= 4) {
                            postalCode = it
                            postalCodeError = null
                        }
                    },
                    label = "Postal Code *",
                    placeholder = "e.g., 1209",
                    keyboardType = KeyboardType.Number,
                    isError = postalCodeError != null,
                    errorMessage = postalCodeError,
                    imeAction = ImeAction.Next
                )

                CustomTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    label = "Landmark (Optional)",
                    placeholder = "e.g., Near Abahani Field",
                    leadingIcon = Icons.Filled.Place,
                    imeAction = ImeAction.Next
                )

                CustomTextField(
                    value = deliveryInstructions,
                    onValueChange = { deliveryInstructions = it },
                    label = "Delivery Instructions (Optional)",
                    placeholder = "e.g., Ring the bell twice, Call before delivery",
                    singleLine = false,
                    maxLines = 3,
                    imeAction = ImeAction.Done
                )

                Divider()

                // Set as Default
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDefault = !isDefault }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Set as default address",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "This will be your primary delivery address",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Required fields note
                Text(
                    text = "* Required fields",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Save Button
                PrimaryButton(
                    text = "Save Address",
                    onClick = { saveAddress() },
                    isLoading = isSaving
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

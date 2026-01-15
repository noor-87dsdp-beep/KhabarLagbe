package com.noor.khabarlagbe.presentation.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.Address
import com.noor.khabarlagbe.presentation.components.AddressCard
import com.noor.khabarlagbe.presentation.components.EmptyState
import com.noor.khabarlagbe.presentation.components.ErrorScreen
import com.noor.khabarlagbe.presentation.components.LoadingIndicator
import com.noor.khabarlagbe.presentation.components.PrimaryButton
import com.noor.khabarlagbe.presentation.profile.addresses.AddressUiState
import com.noor.khabarlagbe.presentation.profile.addresses.AddressViewModel
import com.noor.khabarlagbe.ui.theme.Primary

/**
 * Address Selection Screen
 * 
 * Features:
 * - List of saved addresses with radio button selection
 * - Edit and delete buttons for each address
 * - Add new address FAB
 * - Confirm selection button
 * - Integration with AddressViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSelectionScreen(
    navController: NavController,
    viewModel: AddressViewModel = hiltViewModel(),
    onAddressSelected: (Address) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val addressActionState by viewModel.addressActionState.collectAsState()
    var selectedAddressId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<String?>(null) }

    // Handle address action state
    LaunchedEffect(addressActionState) {
        when (addressActionState) {
            is com.noor.khabarlagbe.presentation.profile.addresses.AddressActionState.Success -> {
                viewModel.loadAddresses()
                viewModel.resetActionState()
            }
            else -> {}
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && addressToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                addressToDelete = null
            },
            title = { Text("Delete Address") },
            text = { Text("Are you sure you want to delete this address?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        addressToDelete?.let { viewModel.deleteAddress(it) }
                        showDeleteDialog = false
                        addressToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    addressToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Address") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to AddAddressScreen
                    navController.navigate("add_address")
                },
                containerColor = Primary
            ) {
                Icon(Icons.Filled.Add, "Add Address")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AddressUiState.Loading -> {
                    LoadingIndicator()
                }

                is AddressUiState.Empty -> {
                    EmptyState(
                        icon = Icons.Filled.LocationOn,
                        title = "No Addresses",
                        message = "Add an address to continue with your order",
                        actionButtonText = "Add Address",
                        onActionClick = {
                            navController.navigate("add_address")
                        }
                    )
                }

                is AddressUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.addresses) { address ->
                                AddressSelectionCard(
                                    address = address,
                                    isSelected = address.id == selectedAddressId,
                                    onSelect = {
                                        selectedAddressId = address.id
                                    },
                                    onEdit = {
                                        // Navigate to edit address
                                        navController.navigate("edit_address/${address.id}")
                                    },
                                    onDelete = {
                                        addressToDelete = address.id
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }

                        // Confirm Button
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
                                    text = "Confirm Address",
                                    onClick = {
                                        selectedAddressId?.let { id ->
                                            state.addresses.find { it.id == id }?.let { address ->
                                                onAddressSelected(address)
                                                navController.navigateUp()
                                            }
                                        }
                                    },
                                    enabled = selectedAddressId != null
                                )
                            }
                        }
                    }
                }

                is AddressUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

/**
 * Address card with radio button selection
 */
@Composable
private fun AddressSelectionCard(
    address: Address,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = address.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )

                    if (address.isDefault) {
                        Surface(
                            color = Primary.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Default",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildString {
                        append("${address.houseNo}, ${address.roadNo}\n")
                        append("${address.area}, ${address.thana}\n")
                        append("${address.district} - ${address.postalCode}")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!address.deliveryInstructions.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Note: ${address.deliveryInstructions}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onEdit) {
                        Text("Edit")
                    }
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

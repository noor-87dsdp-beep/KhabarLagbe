package com.noor.khabarlagbe.restaurant.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMenuItemScreen(
    itemId: String?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val menuItemState by viewModel.menuItemState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val isEditing = itemId != null && itemId != "new"
    
    LaunchedEffect(itemId) {
        if (isEditing) {
            viewModel.loadMenuItem(itemId!!)
        } else {
            viewModel.clearForm()
        }
    }
    
    LaunchedEffect(menuItemState) {
        if (menuItemState is MenuItemUiState.Success && isEditing) {
            // Item loaded for editing
        } else if (menuItemState is MenuItemUiState.Success && !isEditing) {
            onSaveSuccess()
        }
    }
    
    val categories = when (uiState) {
        is MenuUiState.Success -> (uiState as MenuUiState.Success).categories
        else -> emptyList()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "আইটেম সম্পাদনা" else "নতুন আইটেম") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveMenuItem(if (isEditing) itemId else null) },
                        enabled = formState.name.isNotBlank() && formState.categoryId.isNotBlank() && formState.price.isNotBlank()
                    ) {
                        if (menuItemState is MenuItemUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text("সেভ করুন")
                        }
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image upload placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("ছবি আপলোড করুন")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { /* Image picker */ }) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ছবি নির্বাচন")
                        }
                    }
                }
            }
            
            // Category dropdown
            var categoryExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == formState.categoryId }?.name ?: "",
                    onValueChange = {},
                    label = { Text("ক্যাটেগরি *") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                viewModel.updateFormState { copy(categoryId = category.id) }
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Basic info
            Text(
                text = "মৌলিক তথ্য",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = formState.name,
                onValueChange = { viewModel.updateFormState { copy(name = it) } },
                label = { Text("আইটেমের নাম (বাংলা) *") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = formState.nameEn,
                onValueChange = { viewModel.updateFormState { copy(nameEn = it) } },
                label = { Text("আইটেমের নাম (English)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = formState.description,
                onValueChange = { viewModel.updateFormState { copy(description = it) } },
                label = { Text("বিবরণ (বাংলা)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            OutlinedTextField(
                value = formState.descriptionEn,
                onValueChange = { viewModel.updateFormState { copy(descriptionEn = it) } },
                label = { Text("বিবরণ (English)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            // Pricing
            Text(
                text = "মূল্য",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = formState.price,
                    onValueChange = { viewModel.updateFormState { copy(price = it) } },
                    label = { Text("দাম *") },
                    prefix = { Text("৳") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = formState.discountedPrice,
                    onValueChange = { viewModel.updateFormState { copy(discountedPrice = it) } },
                    label = { Text("ছাড়ের দাম") },
                    prefix = { Text("৳") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Dietary flags
            Text(
                text = "খাদ্যতালিকা",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = formState.isVegetarian,
                    onClick = { viewModel.updateFormState { copy(isVegetarian = !isVegetarian) } },
                    label = { Text("নিরামিষ") },
                    leadingIcon = if (formState.isVegetarian) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
                FilterChip(
                    selected = formState.isVegan,
                    onClick = { viewModel.updateFormState { copy(isVegan = !isVegan) } },
                    label = { Text("ভেগান") },
                    leadingIcon = if (formState.isVegan) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
                FilterChip(
                    selected = formState.isGlutenFree,
                    onClick = { viewModel.updateFormState { copy(isGlutenFree = !isGlutenFree) } },
                    label = { Text("গ্লুটেন মুক্ত") },
                    leadingIcon = if (formState.isGlutenFree) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = formState.isSpicy,
                    onCheckedChange = { viewModel.updateFormState { copy(isSpicy = it) } }
                )
                Text("ঝাল")
                if (formState.isSpicy) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("ঝালের মাত্রা:")
                    Slider(
                        value = formState.spicyLevel.toFloat(),
                        onValueChange = { viewModel.updateFormState { copy(spicyLevel = it.toInt()) } },
                        valueRange = 1f..5f,
                        steps = 3,
                        modifier = Modifier.weight(1f)
                    )
                    Text("${formState.spicyLevel}")
                }
            }
            
            // Additional info
            Text(
                text = "অতিরিক্ত তথ্য",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = formState.preparationTime,
                    onValueChange = { viewModel.updateFormState { copy(preparationTime = it) } },
                    label = { Text("প্রস্তুতির সময় (মিনিট)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = formState.calories,
                    onValueChange = { viewModel.updateFormState { copy(calories = it) } },
                    label = { Text("ক্যালোরি") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            
            OutlinedTextField(
                value = formState.tags,
                onValueChange = { viewModel.updateFormState { copy(tags = it) } },
                label = { Text("ট্যাগ (কমা দিয়ে আলাদা করুন)") },
                placeholder = { Text("যেমন: বেস্টসেলার, নতুন, জনপ্রিয়") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Availability toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("আইটেম উপলব্ধ")
                Switch(
                    checked = formState.isAvailable,
                    onCheckedChange = { viewModel.updateFormState { copy(isAvailable = it) } }
                )
            }
            
            if (menuItemState is MenuItemUiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = (menuItemState as MenuItemUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

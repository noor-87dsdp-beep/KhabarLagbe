package com.noor.khabarlagbe.restaurant.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noor.khabarlagbe.restaurant.domain.model.MenuCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    onBackClick: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryState by viewModel.categoryState.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<MenuCategory?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    var categoryName by remember { mutableStateOf("") }
    var categoryNameEn by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    var categorySortOrder by remember { mutableStateOf("0") }
    
    val categories = when (uiState) {
        is MenuUiState.Success -> (uiState as MenuUiState.Success).categories
        else -> emptyList()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ক্যাটেগরি ম্যানেজমেন্ট") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    categoryName = ""
                    categoryNameEn = ""
                    categoryDescription = ""
                    categorySortOrder = "${categories.size}"
                    showAddDialog = true 
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("নতুন ক্যাটেগরি") }
            )
        }
    ) { padding ->
        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "কোনো ক্যাটেগরি নেই",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "নতুন ক্যাটেগরি যোগ করুন",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryManagementCard(
                        category = category,
                        onEdit = {
                            categoryName = category.name
                            categoryNameEn = category.nameEn ?: ""
                            categoryDescription = category.description ?: ""
                            categorySortOrder = category.sortOrder.toString()
                            editingCategory = category
                            showAddDialog = true
                        },
                        onDelete = { showDeleteDialog = category.id },
                        onToggleActive = {
                            viewModel.updateCategory(
                                categoryId = category.id,
                                name = null,
                                nameEn = null,
                                description = null,
                                sortOrder = null,
                                isActive = !category.isActive
                            )
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingCategory = null
            },
            title = { 
                Text(if (editingCategory != null) "ক্যাটেগরি সম্পাদনা" else "নতুন ক্যাটেগরি") 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("নাম (বাংলা) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = categoryNameEn,
                        onValueChange = { categoryNameEn = it },
                        label = { Text("নাম (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = categoryDescription,
                        onValueChange = { categoryDescription = it },
                        label = { Text("বিবরণ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = categorySortOrder,
                        onValueChange = { categorySortOrder = it },
                        label = { Text("ক্রম") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingCategory != null) {
                            viewModel.updateCategory(
                                categoryId = editingCategory!!.id,
                                name = categoryName,
                                nameEn = categoryNameEn.ifEmpty { null },
                                description = categoryDescription.ifEmpty { null },
                                sortOrder = categorySortOrder.toIntOrNull(),
                                isActive = null
                            )
                        } else {
                            viewModel.createCategory(
                                name = categoryName,
                                nameEn = categoryNameEn.ifEmpty { null },
                                description = categoryDescription.ifEmpty { null },
                                sortOrder = categorySortOrder.toIntOrNull() ?: 0
                            )
                        }
                        showAddDialog = false
                        editingCategory = null
                    },
                    enabled = categoryName.isNotBlank()
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingCategory = null
                }) {
                    Text("বাতিল")
                }
            }
        )
    }
    
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("ক্যাটেগরি মুছুন?") },
            text = { Text("এই ক্যাটেগরি এবং এর সব আইটেম মুছে ফেলা হবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(showDeleteDialog!!)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun CategoryManagementCard(
    category: MenuCategory,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (!category.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "নিষ্ক্রিয়",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                if (category.nameEn != null) {
                    Text(
                        text = category.nameEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${category.itemCount} আইটেম",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                IconButton(onClick = onToggleActive) {
                    Icon(
                        if (category.isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (category.isActive) "Hide" else "Show"
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

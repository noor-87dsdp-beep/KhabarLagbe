package com.noor.khabarlagbe.restaurant.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noor.khabarlagbe.restaurant.presentation.components.CategoryCard
import com.noor.khabarlagbe.restaurant.presentation.components.MenuItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    onBackClick: () -> Unit,
    onAddItem: () -> Unit,
    onEditItem: (String) -> Unit,
    onManageCategories: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val expandedCategories by viewModel.expandedCategories.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("মেনু ম্যানেজমেন্ট") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onManageCategories) {
                        Icon(Icons.Default.Category, contentDescription = "Categories")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddItem,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("নতুন আইটেম") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.search(it)
                },
                placeholder = { Text("মেনু খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = ""
                            viewModel.search("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
            
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState) {
                    is MenuUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is MenuUiState.Success -> {
                        val state = uiState as MenuUiState.Success
                        
                        if (state.categories.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "মেনু খালি",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = onManageCategories) {
                                        Text("ক্যাটেগরি যোগ করুন")
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                state.categories.forEach { category ->
                                    val isExpanded = expandedCategories.contains(category.id)
                                    val categoryItems = state.filteredItems.filter { it.categoryId == category.id }
                                    
                                    item(key = "category_${category.id}") {
                                        CategoryCard(
                                            category = category,
                                            isExpanded = isExpanded,
                                            itemCount = categoryItems.size,
                                            onToggleExpand = { viewModel.toggleCategoryExpanded(category.id) },
                                            onEdit = { onManageCategories() },
                                            onDelete = { showDeleteDialog = category.id }
                                        )
                                    }
                                    
                                    if (isExpanded) {
                                        items(
                                            items = categoryItems,
                                            key = { "item_${it.id}" }
                                        ) { item ->
                                            MenuItemCard(
                                                item = item,
                                                onToggleAvailability = { viewModel.toggleItemAvailability(item.id) },
                                                onClick = { onEditItem(item.id) },
                                                modifier = Modifier.padding(start = 16.dp)
                                            )
                                        }
                                    }
                                }
                                
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                    is MenuUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = (uiState as MenuUiState.Error).message,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.loadMenu() }) {
                                    Text("আবার চেষ্টা করুন")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("ক্যাটেগরি মুছুন?") },
            text = { Text("এই ক্যাটেগরি এবং এর সব আইটেম মুছে ফেলা হবে। এই কাজ পূর্বাবস্থায় ফেরানো যাবে না।") },
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

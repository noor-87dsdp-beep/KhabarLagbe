package com.noor.khabarlagbe.restaurant.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.MenuCategory
import com.noor.khabarlagbe.restaurant.domain.model.MenuItem
import com.noor.khabarlagbe.restaurant.domain.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MenuUiState {
    object Loading : MenuUiState()
    data class Success(
        val categories: List<MenuCategory>,
        val items: List<MenuItem>,
        val filteredItems: List<MenuItem>,
        val selectedCategory: String?,
        val searchQuery: String
    ) : MenuUiState()
    data class Error(val message: String) : MenuUiState()
}

sealed class MenuItemUiState {
    object Loading : MenuItemUiState()
    object Idle : MenuItemUiState()
    data class Success(val item: MenuItem) : MenuItemUiState()
    data class Error(val message: String) : MenuItemUiState()
}

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    object Idle : CategoryUiState()
    data class Success(val categories: List<MenuCategory>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

data class MenuItemFormState(
    val categoryId: String = "",
    val name: String = "",
    val nameEn: String = "",
    val description: String = "",
    val descriptionEn: String = "",
    val price: String = "",
    val discountedPrice: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isSpicy: Boolean = false,
    val spicyLevel: Int = 0,
    val preparationTime: String = "15",
    val calories: String = "",
    val tags: String = ""
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()
    
    private val _menuItemState = MutableStateFlow<MenuItemUiState>(MenuItemUiState.Idle)
    val menuItemState: StateFlow<MenuItemUiState> = _menuItemState.asStateFlow()
    
    private val _categoryState = MutableStateFlow<CategoryUiState>(CategoryUiState.Idle)
    val categoryState: StateFlow<CategoryUiState> = _categoryState.asStateFlow()
    
    private val _formState = MutableStateFlow(MenuItemFormState())
    val formState: StateFlow<MenuItemFormState> = _formState.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _expandedCategories = MutableStateFlow<Set<String>>(emptySet())
    val expandedCategories: StateFlow<Set<String>> = _expandedCategories.asStateFlow()
    
    private var allItems: List<MenuItem> = emptyList()
    private var allCategories: List<MenuCategory> = emptyList()
    
    init {
        loadMenu()
        observeMenu()
    }
    
    private fun observeMenu() {
        viewModelScope.launch {
            combine(
                menuRepository.getAllCategories(),
                menuRepository.getMenuItems()
            ) { categories, items ->
                Pair(categories, items)
            }.collect { (categories, items) ->
                allCategories = categories
                allItems = items
                updateUiState()
            }
        }
    }
    
    private fun updateUiState() {
        val currentState = _uiState.value
        val selectedCategory = if (currentState is MenuUiState.Success) currentState.selectedCategory else null
        val searchQuery = if (currentState is MenuUiState.Success) currentState.searchQuery else ""
        
        val filteredItems = filterItems(allItems, selectedCategory, searchQuery)
        
        _uiState.value = MenuUiState.Success(
            categories = allCategories,
            items = allItems,
            filteredItems = filteredItems,
            selectedCategory = selectedCategory,
            searchQuery = searchQuery
        )
    }
    
    private fun filterItems(items: List<MenuItem>, categoryId: String?, query: String): List<MenuItem> {
        return items.filter { item ->
            val matchesCategory = categoryId == null || item.categoryId == categoryId
            val matchesQuery = query.isEmpty() || 
                item.name.contains(query, ignoreCase = true) ||
                item.nameEn?.contains(query, ignoreCase = true) == true
            matchesCategory && matchesQuery
        }
    }
    
    fun loadMenu() {
        viewModelScope.launch {
            _uiState.value = MenuUiState.Loading
            menuRepository.refreshMenu()
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            menuRepository.refreshMenu()
            _isRefreshing.value = false
        }
    }
    
    fun selectCategory(categoryId: String?) {
        val currentState = _uiState.value
        if (currentState is MenuUiState.Success) {
            val filteredItems = filterItems(allItems, categoryId, currentState.searchQuery)
            _uiState.value = currentState.copy(
                selectedCategory = categoryId,
                filteredItems = filteredItems
            )
        }
    }
    
    fun search(query: String) {
        val currentState = _uiState.value
        if (currentState is MenuUiState.Success) {
            val filteredItems = filterItems(allItems, currentState.selectedCategory, query)
            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredItems = filteredItems
            )
        }
    }
    
    fun toggleCategoryExpanded(categoryId: String) {
        _expandedCategories.value = if (_expandedCategories.value.contains(categoryId)) {
            _expandedCategories.value - categoryId
        } else {
            _expandedCategories.value + categoryId
        }
    }
    
    fun toggleItemAvailability(itemId: String) {
        viewModelScope.launch {
            menuRepository.toggleItemAvailability(itemId)
        }
    }
    
    fun loadMenuItem(itemId: String) {
        viewModelScope.launch {
            _menuItemState.value = MenuItemUiState.Loading
            menuRepository.getMenuItemById(itemId)
                .onSuccess { item ->
                    _menuItemState.value = MenuItemUiState.Success(item)
                    _formState.value = MenuItemFormState(
                        categoryId = item.categoryId,
                        name = item.name,
                        nameEn = item.nameEn ?: "",
                        description = item.description ?: "",
                        descriptionEn = item.descriptionEn ?: "",
                        price = item.price.toString(),
                        discountedPrice = item.discountedPrice?.toString() ?: "",
                        imageUrl = item.imageUrl ?: "",
                        isAvailable = item.isAvailable,
                        isVegetarian = item.isVegetarian,
                        isVegan = item.isVegan,
                        isGlutenFree = item.isGlutenFree,
                        isSpicy = item.isSpicy,
                        spicyLevel = item.spicyLevel,
                        preparationTime = item.preparationTime.toString(),
                        calories = item.calories?.toString() ?: "",
                        tags = item.tags?.joinToString(", ") ?: ""
                    )
                }
                .onFailure { error ->
                    _menuItemState.value = MenuItemUiState.Error(error.message ?: "Failed to load item")
                }
        }
    }
    
    fun updateFormState(update: MenuItemFormState.() -> MenuItemFormState) {
        _formState.value = _formState.value.update()
    }
    
    fun clearForm() {
        _formState.value = MenuItemFormState()
        _menuItemState.value = MenuItemUiState.Idle
    }
    
    fun saveMenuItem(itemId: String?) {
        viewModelScope.launch {
            _menuItemState.value = MenuItemUiState.Loading
            val form = _formState.value
            val item = MenuItem(
                id = itemId ?: "",
                categoryId = form.categoryId,
                name = form.name,
                nameEn = form.nameEn.ifEmpty { null },
                description = form.description.ifEmpty { null },
                descriptionEn = form.descriptionEn.ifEmpty { null },
                price = form.price.toDoubleOrNull() ?: 0.0,
                discountedPrice = form.discountedPrice.toDoubleOrNull(),
                imageUrl = form.imageUrl.ifEmpty { null },
                isAvailable = form.isAvailable,
                isVegetarian = form.isVegetarian,
                isVegan = form.isVegan,
                isGlutenFree = form.isGlutenFree,
                isSpicy = form.isSpicy,
                spicyLevel = form.spicyLevel,
                preparationTime = form.preparationTime.toIntOrNull() ?: 15,
                calories = form.calories.toIntOrNull(),
                customizations = null,
                tags = form.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { null },
                sortOrder = 0
            )
            
            val result = if (itemId.isNullOrEmpty()) {
                menuRepository.createMenuItem(item)
            } else {
                menuRepository.updateMenuItem(itemId, item)
            }
            
            result
                .onSuccess { savedItem ->
                    _menuItemState.value = MenuItemUiState.Success(savedItem)
                }
                .onFailure { error ->
                    _menuItemState.value = MenuItemUiState.Error(error.message ?: "Failed to save item")
                }
        }
    }
    
    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            menuRepository.deleteMenuItem(itemId)
        }
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            _categoryState.value = CategoryUiState.Loading
            menuRepository.getAllCategories().first().let { categories ->
                _categoryState.value = CategoryUiState.Success(categories)
            }
        }
    }
    
    fun createCategory(name: String, nameEn: String?, description: String?, sortOrder: Int) {
        viewModelScope.launch {
            _categoryState.value = CategoryUiState.Loading
            menuRepository.createCategory(name, nameEn, description, sortOrder)
                .onSuccess { loadCategories() }
                .onFailure { error ->
                    _categoryState.value = CategoryUiState.Error(error.message ?: "Failed to create category")
                }
        }
    }
    
    fun updateCategory(categoryId: String, name: String?, nameEn: String?, description: String?, sortOrder: Int?, isActive: Boolean?) {
        viewModelScope.launch {
            menuRepository.updateCategory(categoryId, name, nameEn, description, sortOrder, isActive)
                .onSuccess { loadCategories() }
        }
    }
    
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            menuRepository.deleteCategory(categoryId)
        }
    }
}

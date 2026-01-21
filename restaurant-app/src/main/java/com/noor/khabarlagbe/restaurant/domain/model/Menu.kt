package com.noor.khabarlagbe.restaurant.domain.model

data class MenuCategory(
    val id: String,
    val name: String,
    val nameEn: String?,
    val description: String?,
    val imageUrl: String?,
    val sortOrder: Int,
    val isActive: Boolean,
    val itemCount: Int
)

data class MenuItem(
    val id: String,
    val categoryId: String,
    val name: String,
    val nameEn: String?,
    val description: String?,
    val descriptionEn: String?,
    val price: Double,
    val discountedPrice: Double?,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val isSpicy: Boolean,
    val spicyLevel: Int,
    val preparationTime: Int,
    val calories: Int?,
    val customizations: List<MenuCustomization>?,
    val tags: List<String>?,
    val sortOrder: Int
)

data class MenuCustomization(
    val id: String,
    val name: String,
    val nameEn: String?,
    val required: Boolean,
    val multiSelect: Boolean,
    val maxSelections: Int?,
    val options: List<CustomizationOption>
)

data class CustomizationOption(
    val id: String,
    val name: String,
    val nameEn: String?,
    val price: Double,
    val isDefault: Boolean
)

data class MenuWithCategories(
    val categories: List<MenuCategory>,
    val items: List<MenuItem>
)

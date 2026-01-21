package com.noor.khabarlagbe.restaurant.data.dto

import com.google.gson.annotations.SerializedName

data class MenuCategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("sortOrder") val sortOrder: Int,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("itemCount") val itemCount: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class MenuItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("descriptionEn") val descriptionEn: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("discountedPrice") val discountedPrice: Double?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isAvailable") val isAvailable: Boolean,
    @SerializedName("isVegetarian") val isVegetarian: Boolean,
    @SerializedName("isVegan") val isVegan: Boolean,
    @SerializedName("isGlutenFree") val isGlutenFree: Boolean,
    @SerializedName("isSpicy") val isSpicy: Boolean,
    @SerializedName("spicyLevel") val spicyLevel: Int,
    @SerializedName("preparationTime") val preparationTime: Int,
    @SerializedName("calories") val calories: Int?,
    @SerializedName("customizations") val customizations: List<MenuCustomizationDto>?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("sortOrder") val sortOrder: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class MenuCustomizationDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("required") val required: Boolean,
    @SerializedName("multiSelect") val multiSelect: Boolean,
    @SerializedName("maxSelections") val maxSelections: Int?,
    @SerializedName("options") val options: List<CustomizationOptionDto>
)

data class CustomizationOptionDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("isDefault") val isDefault: Boolean
)

data class MenuResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MenuDataDto?
)

data class MenuDataDto(
    @SerializedName("categories") val categories: List<MenuCategoryDto>,
    @SerializedName("items") val items: List<MenuItemDto>
)

data class CategoriesResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<MenuCategoryDto>?
)

data class CategoryResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: MenuCategoryDto?
)

data class CreateCategoryRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("sortOrder") val sortOrder: Int
)

data class UpdateCategoryRequestDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("nameEn") val nameEn: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("sortOrder") val sortOrder: Int? = null,
    @SerializedName("isActive") val isActive: Boolean? = null
)

data class MenuItemsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<MenuItemDto>?
)

data class MenuItemResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: MenuItemDto?
)

data class CreateMenuItemRequestDto(
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("descriptionEn") val descriptionEn: String?,
    @SerializedName("price") val price: Double,
    @SerializedName("discountedPrice") val discountedPrice: Double?,
    @SerializedName("isVegetarian") val isVegetarian: Boolean,
    @SerializedName("isVegan") val isVegan: Boolean,
    @SerializedName("isGlutenFree") val isGlutenFree: Boolean,
    @SerializedName("isSpicy") val isSpicy: Boolean,
    @SerializedName("spicyLevel") val spicyLevel: Int,
    @SerializedName("preparationTime") val preparationTime: Int,
    @SerializedName("calories") val calories: Int?,
    @SerializedName("customizations") val customizations: List<MenuCustomizationDto>?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("sortOrder") val sortOrder: Int
)

data class UpdateMenuItemRequestDto(
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("nameEn") val nameEn: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("descriptionEn") val descriptionEn: String? = null,
    @SerializedName("price") val price: Double? = null,
    @SerializedName("discountedPrice") val discountedPrice: Double? = null,
    @SerializedName("isAvailable") val isAvailable: Boolean? = null,
    @SerializedName("isVegetarian") val isVegetarian: Boolean? = null,
    @SerializedName("isVegan") val isVegan: Boolean? = null,
    @SerializedName("isGlutenFree") val isGlutenFree: Boolean? = null,
    @SerializedName("isSpicy") val isSpicy: Boolean? = null,
    @SerializedName("spicyLevel") val spicyLevel: Int? = null,
    @SerializedName("preparationTime") val preparationTime: Int? = null,
    @SerializedName("calories") val calories: Int? = null,
    @SerializedName("customizations") val customizations: List<MenuCustomizationDto>? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("sortOrder") val sortOrder: Int? = null
)

data class ToggleAvailabilityResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: AvailabilityDataDto?
)

data class AvailabilityDataDto(
    @SerializedName("isAvailable") val isAvailable: Boolean
)

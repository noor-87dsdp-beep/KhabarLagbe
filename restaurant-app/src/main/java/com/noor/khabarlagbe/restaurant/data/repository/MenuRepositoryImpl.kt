package com.noor.khabarlagbe.restaurant.data.repository

import com.noor.khabarlagbe.restaurant.data.api.RestaurantApi
import com.noor.khabarlagbe.restaurant.data.dto.*
import com.noor.khabarlagbe.restaurant.data.local.dao.MenuCategoryDao
import com.noor.khabarlagbe.restaurant.data.local.dao.MenuItemDao
import com.noor.khabarlagbe.restaurant.data.local.entity.MenuCategoryEntity
import com.noor.khabarlagbe.restaurant.data.local.entity.MenuItemEntity
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.MenuRepository
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val api: RestaurantApi,
    private val categoryDao: MenuCategoryDao,
    private val menuItemDao: MenuItemDao,
    private val authRepository: RestaurantAuthRepository
) : MenuRepository {
    
    private fun getAuthToken(): String {
        return "Bearer ${authRepository.getToken() ?: ""}"
    }
    
    override fun getCategories(): Flow<List<MenuCategory>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getAllCategories(): Flow<List<MenuCategory>> {
        return categoryDao.getAllCategoriesIncludingInactive().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAllMenuItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getMenuItemsByCategory(categoryId: String): Flow<List<MenuItem>> {
        return menuItemDao.getMenuItemsByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchMenuItems(query: String): Flow<List<MenuItem>> {
        return menuItemDao.searchMenuItems(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getMenuItemById(itemId: String): Result<MenuItem> {
        return try {
            val response = api.getMenuItem(getAuthToken(), itemId)
            if (response.isSuccessful && response.body()?.success == true) {
                val item = response.body()!!.data!!.toDomain()
                Result.success(item)
            } else {
                val cached = menuItemDao.getMenuItemById(itemId)
                if (cached != null) {
                    Result.success(cached.toDomain())
                } else {
                    Result.failure(Exception("Item not found"))
                }
            }
        } catch (e: Exception) {
            val cached = menuItemDao.getMenuItemById(itemId)
            if (cached != null) {
                Result.success(cached.toDomain())
            } else {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun createCategory(name: String, nameEn: String?, description: String?, sortOrder: Int): Result<MenuCategory> {
        return try {
            val request = CreateCategoryRequestDto(name, nameEn, description, sortOrder)
            val response = api.createCategory(getAuthToken(), request)
            if (response.isSuccessful && response.body()?.success == true) {
                val category = response.body()!!.data!!.toDomain()
                categoryDao.insertCategory(category.toEntity())
                Result.success(category)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to create category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCategory(
        categoryId: String,
        name: String?,
        nameEn: String?,
        description: String?,
        sortOrder: Int?,
        isActive: Boolean?
    ): Result<MenuCategory> {
        return try {
            val request = UpdateCategoryRequestDto(name, nameEn, description, sortOrder, isActive)
            val response = api.updateCategory(getAuthToken(), categoryId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                val category = response.body()!!.data!!.toDomain()
                categoryDao.updateCategory(category.toEntity())
                Result.success(category)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            val response = api.deleteCategory(getAuthToken(), categoryId)
            if (response.isSuccessful) {
                categoryDao.deleteCategoryById(categoryId)
                menuItemDao.deleteMenuItemsByCategory(categoryId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createMenuItem(item: MenuItem): Result<MenuItem> {
        return try {
            val request = CreateMenuItemRequestDto(
                categoryId = item.categoryId,
                name = item.name,
                nameEn = item.nameEn,
                description = item.description,
                descriptionEn = item.descriptionEn,
                price = item.price,
                discountedPrice = item.discountedPrice,
                isVegetarian = item.isVegetarian,
                isVegan = item.isVegan,
                isGlutenFree = item.isGlutenFree,
                isSpicy = item.isSpicy,
                spicyLevel = item.spicyLevel,
                preparationTime = item.preparationTime,
                calories = item.calories,
                customizations = item.customizations?.map { it.toDto() },
                tags = item.tags,
                sortOrder = item.sortOrder
            )
            val response = api.createMenuItem(getAuthToken(), request)
            if (response.isSuccessful && response.body()?.success == true) {
                val createdItem = response.body()!!.data!!.toDomain()
                menuItemDao.insertMenuItem(createdItem.toEntity())
                Result.success(createdItem)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to create item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMenuItem(itemId: String, item: MenuItem): Result<MenuItem> {
        return try {
            val request = UpdateMenuItemRequestDto(
                categoryId = item.categoryId,
                name = item.name,
                nameEn = item.nameEn,
                description = item.description,
                descriptionEn = item.descriptionEn,
                price = item.price,
                discountedPrice = item.discountedPrice,
                isAvailable = item.isAvailable,
                isVegetarian = item.isVegetarian,
                isVegan = item.isVegan,
                isGlutenFree = item.isGlutenFree,
                isSpicy = item.isSpicy,
                spicyLevel = item.spicyLevel,
                preparationTime = item.preparationTime,
                calories = item.calories,
                customizations = item.customizations?.map { it.toDto() },
                tags = item.tags,
                sortOrder = item.sortOrder
            )
            val response = api.updateMenuItem(getAuthToken(), itemId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                val updatedItem = response.body()!!.data!!.toDomain()
                menuItemDao.updateMenuItem(updatedItem.toEntity())
                Result.success(updatedItem)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteMenuItem(itemId: String): Result<Unit> {
        return try {
            val response = api.deleteMenuItem(getAuthToken(), itemId)
            if (response.isSuccessful) {
                menuItemDao.deleteMenuItemById(itemId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun toggleItemAvailability(itemId: String): Result<Boolean> {
        return try {
            val response = api.toggleItemAvailability(getAuthToken(), itemId)
            if (response.isSuccessful && response.body()?.success == true) {
                val isAvailable = response.body()!!.data!!.isAvailable
                menuItemDao.updateAvailability(itemId, isAvailable)
                Result.success(isAvailable)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to toggle availability"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshMenu(): Result<MenuWithCategories> {
        return try {
            val response = api.getMenu(getAuthToken())
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                val categories = data.categories.map { it.toDomain() }
                val items = data.items.map { it.toDomain() }
                
                categoryDao.clearCategories()
                categoryDao.insertCategories(categories.map { it.toEntity() })
                
                menuItemDao.clearMenuItems()
                menuItemDao.insertMenuItems(items.map { it.toEntity() })
                
                Result.success(MenuWithCategories(categories, items))
            } else {
                Result.failure(Exception("Failed to fetch menu"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun MenuCategoryDto.toDomain(): MenuCategory {
        return MenuCategory(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            imageUrl = imageUrl,
            sortOrder = sortOrder,
            isActive = isActive,
            itemCount = itemCount
        )
    }
    
    private fun MenuItemDto.toDomain(): MenuItem {
        return MenuItem(
            id = id,
            categoryId = categoryId,
            name = name,
            nameEn = nameEn,
            description = description,
            descriptionEn = descriptionEn,
            price = price,
            discountedPrice = discountedPrice,
            imageUrl = imageUrl,
            isAvailable = isAvailable,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            isGlutenFree = isGlutenFree,
            isSpicy = isSpicy,
            spicyLevel = spicyLevel,
            preparationTime = preparationTime,
            calories = calories,
            customizations = customizations?.map { it.toDomain() },
            tags = tags,
            sortOrder = sortOrder
        )
    }
    
    private fun MenuCustomizationDto.toDomain(): MenuCustomization {
        return MenuCustomization(
            id = id,
            name = name,
            nameEn = nameEn,
            required = required,
            multiSelect = multiSelect,
            maxSelections = maxSelections,
            options = options.map { CustomizationOption(it.id, it.name, it.nameEn, it.price, it.isDefault) }
        )
    }
    
    private fun MenuCustomization.toDto(): MenuCustomizationDto {
        return MenuCustomizationDto(
            id = id,
            name = name,
            nameEn = nameEn,
            required = required,
            multiSelect = multiSelect,
            maxSelections = maxSelections,
            options = options.map { CustomizationOptionDto(it.id, it.name, it.nameEn, it.price, it.isDefault) }
        )
    }
    
    private fun MenuCategory.toEntity(): MenuCategoryEntity {
        return MenuCategoryEntity(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            imageUrl = imageUrl,
            sortOrder = sortOrder,
            isActive = isActive,
            itemCount = itemCount,
            createdAt = "",
            updatedAt = ""
        )
    }
    
    private fun MenuCategoryEntity.toDomain(): MenuCategory {
        return MenuCategory(
            id = id,
            name = name,
            nameEn = nameEn,
            description = description,
            imageUrl = imageUrl,
            sortOrder = sortOrder,
            isActive = isActive,
            itemCount = itemCount
        )
    }
    
    private fun MenuItem.toEntity(): MenuItemEntity {
        return MenuItemEntity(
            id = id,
            categoryId = categoryId,
            name = name,
            nameEn = nameEn,
            description = description,
            descriptionEn = descriptionEn,
            price = price,
            discountedPrice = discountedPrice,
            imageUrl = imageUrl,
            isAvailable = isAvailable,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            isGlutenFree = isGlutenFree,
            isSpicy = isSpicy,
            spicyLevel = spicyLevel,
            preparationTime = preparationTime,
            calories = calories,
            tags = tags,
            sortOrder = sortOrder,
            createdAt = "",
            updatedAt = ""
        )
    }
    
    private fun MenuItemEntity.toDomain(): MenuItem {
        return MenuItem(
            id = id,
            categoryId = categoryId,
            name = name,
            nameEn = nameEn,
            description = description,
            descriptionEn = descriptionEn,
            price = price,
            discountedPrice = discountedPrice,
            imageUrl = imageUrl,
            isAvailable = isAvailable,
            isVegetarian = isVegetarian,
            isVegan = isVegan,
            isGlutenFree = isGlutenFree,
            isSpicy = isSpicy,
            spicyLevel = spicyLevel,
            preparationTime = preparationTime,
            calories = calories,
            customizations = null,
            tags = tags,
            sortOrder = sortOrder
        )
    }
}

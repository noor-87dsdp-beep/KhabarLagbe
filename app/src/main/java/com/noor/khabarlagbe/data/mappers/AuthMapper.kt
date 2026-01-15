package com.noor.khabarlagbe.data.mappers

import com.noor.khabarlagbe.data.local.entity.UserEntity
import com.noor.khabarlagbe.data.remote.dto.UserDto
import com.noor.khabarlagbe.domain.model.User

/**
 * Mapper functions for authentication related models
 */

/**
 * Convert UserDto from API to domain User model
 */
fun UserDto.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        profileImageUrl = profileImageUrl,
        savedAddresses = emptyList(), // Addresses are loaded separately
        createdAt = createdAt
    )
}

/**
 * Convert UserDto to UserEntity for Room database
 */
fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt
    )
}

/**
 * Convert UserEntity from Room database to domain User model
 */
fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        profileImageUrl = profileImageUrl,
        savedAddresses = emptyList(), // Addresses are loaded separately
        createdAt = createdAt
    )
}

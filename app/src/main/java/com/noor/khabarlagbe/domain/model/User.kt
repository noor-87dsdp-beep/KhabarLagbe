package com.noor.khabarlagbe.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val profileImageUrl: String? = null,
    val savedAddresses: List<Address> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Address(
    val id: String,
    val label: String, // Home, Work, Other
    val addressLine1: String,
    val addressLine2: String? = null,
    val city: String,
    val state: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false
)

package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp

data class MenuItem(
    val itemId: String = "",
    val vendorId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "", // e.g., "Burger", "Drink"
    val isAvailable: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
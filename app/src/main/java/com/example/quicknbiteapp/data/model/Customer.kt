package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Customer(
    val customerId: String = "", // Firebase Auth UID
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val deliveryAddress: String = "",
    val favoriteCuisines: List<String> = emptyList(),
    val pushNotifications: Boolean = true,
    val emailNotifications: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val lastLogin: Timestamp = Timestamp.now(),

    // Loyalty/points system
    val loyaltyPoints: Int = 0,
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0
) {
    @Exclude
    fun getDisplayName(): String {
        return fullName.ifEmpty { email.substringBefore("@") }
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            "customerId" to customerId,
            "email" to email,
            "fullName" to fullName,
            "phoneNumber" to phoneNumber,
            "deliveryAddress" to deliveryAddress,
            "favoriteCuisines" to favoriteCuisines,
            "pushNotifications" to pushNotifications,
            "emailNotifications" to emailNotifications,
            "createdAt" to createdAt,
            "lastLogin" to lastLogin,
            "loyaltyPoints" to loyaltyPoints,
            "totalOrders" to totalOrders,
            "totalSpent" to totalSpent
        )
    }
}
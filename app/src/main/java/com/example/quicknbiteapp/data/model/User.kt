package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp

data class User(
    val userId: String = "", // Firebase Auth UID
    val email: String? = null,
    val displayName: String? = null,
    val businessName: String? = null,
    val operatingHours: String = "",
    val businessAddress: String = "",
    val userType: String = "customer", // "customer" or "vendor"
    val phoneNumber: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)
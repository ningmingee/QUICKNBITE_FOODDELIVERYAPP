package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp

data class Vendor(
    val id: String = "",
    val userId: String = "",
    val vendorId: String = "",
    val businessName: String = "",
    val ownerName: String = "",
    val email: String = "",
    val phone: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val operatingHours: String = "",
    val totalCustomers: Int = 0,
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val weeklyRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val topSellingItem: String = "",
    val topSellingCount: Int = 0,
    val userType: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Any?
)

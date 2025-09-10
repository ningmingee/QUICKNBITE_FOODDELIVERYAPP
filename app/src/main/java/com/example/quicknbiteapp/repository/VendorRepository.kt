package com.example.quicknbiteapp.repository

import com.example.quicknbiteapp.data.model.MenuItem
import com.example.quicknbiteapp.data.model.Order
import com.example.quicknbiteapp.data.model.OrderStatus
import com.example.quicknbiteapp.data.model.Review
import com.example.quicknbiteapp.data.model.Vendor

interface VendorRepository {
    suspend fun getVendorData(vendorId: String): Result<Vendor>
    suspend fun getMenuItems(vendorId: String): Result<List<MenuItem>>
    suspend fun getOrders(vendorId: String): Result<List<Order>>
    suspend fun getVendorReviews(vendorId: String): Result<List<Review>>
    suspend fun updateMenuItemAvailability(itemId: String, isAvailable: Boolean): Result<Unit>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
}
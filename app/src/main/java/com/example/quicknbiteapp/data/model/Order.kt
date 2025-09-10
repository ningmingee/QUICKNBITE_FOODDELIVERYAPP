package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Order(
    val orderId: String = "",
    val vendorId: String = "",
    val customerId: String = "",
    val customerName: String? = null,
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val serviceFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val orderType: String = OrderType.DELIVERY.name,
    val deliveryAddress: String? = null,
    val specialInstructions: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val paymentDetails: String? = null,
    val customerPhoneNumber: String? = null
) {
    // Helper function to get creation date
    fun getCreationDate(): Date {
        return createdAt.toDate()
    }
}


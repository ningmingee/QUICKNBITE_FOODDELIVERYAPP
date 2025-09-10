package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Order(
    val orderId: String = "",
    val vendorId: String = "",
    val customerId: String = "",
    val customerName: String? = null,
    val customerPhoneNumber: String? = null,
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val serviceFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val orderType: String = OrderType.DELIVERY.name,
    val statusHistory: Map<String, Any> = emptyMap(),
    val deliveryAddress: String? = null,
    val specialInstructions: String? = null,
    val paymentMethod: String? = null,
    val paymentStatus: String? = null,
    val paymentDetails: String? = null,
    val createdAt: Any = Timestamp.now(),
    val updatedAt: Any = Timestamp.now(),
) {
    // Helper function to safely get creation date
    fun getCreationDate(): Date {
        return when (createdAt) {
            is Timestamp -> createdAt.toDate()
            is Long -> Date(createdAt)
            is Date -> createdAt
            else -> Date()
        }
    }

    // Helper function to safely get update date
    fun getUpdateDate(): Date {
        return when (updatedAt) {
            is Timestamp -> updatedAt.toDate()
            is Long -> Date(updatedAt)
            is Date -> updatedAt
            else -> Date() // Fallback to current date
        }
    }

    // Helper to get status timestamp using OrderStatus enum
    fun getStatusTimestamp(status: OrderStatus): Date? {
        return when (val timestamp = statusHistory[status.name]) {
            is Timestamp -> timestamp.toDate()
            is Long -> Date(timestamp)
            is Date -> timestamp
            else -> null
        }
    }

    // Helper to check if status exists in history using OrderStatus enum
    fun hasStatus(status: OrderStatus): Boolean {
        return statusHistory.containsKey(status.name)
    }

    // Get all status events in chronological order
    fun getStatusEvents(): List<Pair<OrderStatus, Date>> {
        val events = mutableListOf<Pair<OrderStatus, Date>>()

        statusHistory.forEach { (statusKey, timestamp) ->
            val status = OrderStatus.fromString(statusKey)
            val date = when (timestamp) {
                is Timestamp -> timestamp.toDate()
                is Long -> Date(timestamp)
                is Date -> timestamp
                else -> null
            }

            if (date != null) {
                events.add(status to date)
            }
        }

        return events.sortedBy { it.second }
    }
}
package com.example.quicknbiteapp.data.model

enum class OrderStatus {
    PENDING,
    PREPARING,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED;

    companion object {
        fun fromString(value: String): OrderStatus {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                PENDING // Default fallback
            }
        }
    }
}
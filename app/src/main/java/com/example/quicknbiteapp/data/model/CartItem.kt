package com.example.quicknbiteapp.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

data class CartItem(
    override val name: String,
    override val price: Double,
    override val imageRes: Int,
    val initialQuantity: Int = 1
) : Orderable {
    var quantity by mutableIntStateOf(initialQuantity)
        private set

    fun increaseQuantity(amount: Int = 1) {
        quantity += amount
    }

    fun decreaseQuantity(amount: Int = 1) {
        quantity = (quantity - amount).coerceAtLeast(0)
    }

    fun getTotalPrice(): Double = price * quantity
}

enum class DeliveryOption(val fee: Double, val label: String) {
    PRIORITY(5.0, "Priority (10-20 min)"),
    STANDARD(3.0, "Standard (30-45 min)")
}

data class CartSummary(
    val subtotal: Double,
    val deliveryFee: Double,
    val taxRate: Double = 0.06
) {
    val tax: Double get() = subtotal * taxRate
    val total: Double get() = subtotal + tax + deliveryFee
}

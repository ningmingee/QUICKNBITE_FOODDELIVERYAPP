package com.example.quicknbiteapp.data.model

data class OrderItem(
    val menuItemId: String = "",
    val name: String = "",
    val pricePerItem: Double = 0.0,
    val quantity: Int = 1,
    val specialInstructions: String? = null,
    val category: String? = null
) {
    fun getItemName(): String = name.ifEmpty { "Unknown Item" }
}

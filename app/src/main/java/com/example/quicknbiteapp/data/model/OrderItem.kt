package com.example.quicknbiteapp.data.model

data class OrderItem(
    val menuItemId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val pricePerItem: Double = 0.0,
    val totalPrice: Double = 0.0
){
    @JvmName("calculateItemDisplayName")
    fun getItemName(): String {
        return name.ifEmpty { name }
    }
}

package com.example.quicknbiteapp.data.model

data class PromoItem(
    override val name: String,
    override val price: Double,
    override val imageRes: Int,
    val discountPercent: Int
) : Orderable {

    fun getDiscountedPrice(): Double = price * (1 - discountPercent / 100.0)
}
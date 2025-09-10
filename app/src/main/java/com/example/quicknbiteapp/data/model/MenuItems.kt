package com.example.quicknbiteapp.data.model

data class MenuItems(
    override val name: String,
    override val price: Double,
    override val imageRes: Int
) : Orderable
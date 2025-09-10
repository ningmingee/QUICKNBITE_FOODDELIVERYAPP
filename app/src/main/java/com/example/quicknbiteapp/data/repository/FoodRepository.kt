package com.example.quicknbiteapp.data.repository

import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.model.MenuItems

object FoodRepository {

    fun getBurgers(): List<MenuItems> = listOf(
        MenuItems("Classic Burger", 6.0, R.drawable.classic_burger),
        MenuItems("Triple Cheese Burger", 10.0, R.drawable.triple_cheese_burger),
        MenuItems("Truffle Burger", 8.0, R.drawable.truffle_burger),
        MenuItems("Spicy Beast Burger", 12.0, R.drawable.spicy_beast_burger)
    )

    fun getDrinks(): List<MenuItems> = listOf(
        MenuItems("Coca-Cola", 3.5, R.drawable.coke),
        MenuItems("Iced Lemon Tea", 4.0, R.drawable.lemonade),
        MenuItems("Pepsi", 6.0, R.drawable.pepsi),
        MenuItems("100 plus", 6.5, R.drawable.hundredplus)
    )

    fun getLayersBakeshopMenu(): List<MenuItems> = listOf(
        MenuItems("Chocolate Cake", 12.0, R.drawable.chocolate_cake),
        MenuItems("Ice Cream Sundae", 5.0, R.drawable.ice_cream_sundae),
        MenuItems("Mango Ice Cream", 8.0, R.drawable.manggo_ice_cream),
        MenuItems("Blueberry Tart", 6.5, R.drawable.blueberry_tart)
    )
}

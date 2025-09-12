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
        MenuItems("Coca Cola", 3.5, R.drawable.coke),
        MenuItems("Sour Plum Lemonade", 5.0, R.drawable.lemonade),
        MenuItems("Pepsi", 3.5, R.drawable.pepsi),
        MenuItems("100 Plus", 3.5, R.drawable.hundredplus)
    )

    fun getCake(): List<MenuItems> = listOf(
        MenuItems("Chocolate Cake", 12.0, R.drawable.chocolate_cake),
        MenuItems("Rose Cake", 2.0, R.drawable.rose_cake),
        MenuItems("Strawberry Cake", 8.0, R.drawable.strawberry_cake),
        MenuItems("Blueberry Cake", 6.5, R.drawable.blueberry_cake)
    )

    fun getIceCream(): List<MenuItems> = listOf(
    MenuItems("Chocolate Ice Cream", 12.0, R.drawable.chocolate_ice_cream),
    MenuItems("Strawberry Ice Cream", 2.0, R.drawable.strawberry_ice_cream),
    MenuItems("Mango Ice Cream", 8.0, R.drawable.mango_ice_cream),
    MenuItems("Blueberry Ice Cream", 6.5, R.drawable.blueberry_ice_cream)
    )

    fun getMealPlansBreakfast(): List<MenuItems> = listOf(
        MenuItems("Breakfast Set 1", 10.0, R.drawable.breakfast_set_1),
        MenuItems("Breakfast Set 2", 10.0, R.drawable.breakfast_set_2),
        MenuItems("Breakfast Set 3", 10.0, R.drawable.breakfast_set_3)
        )

    fun getMealPlansLunch(): List<MenuItems> = listOf(
        MenuItems("Lunch Set 1", 12.0, R.drawable.lunch_set_1),
        MenuItems("Lunch Set 2", 12.0, R.drawable.lunch_set_2),
        MenuItems("Lunch Set 3", 12.0, R.drawable.lunch_set_3)
    )

    fun getMealPlansDinner(): List<MenuItems> = listOf(
        MenuItems("Dinner Set 1", 11.0, R.drawable.dinner_set_1),
        MenuItems("Dinner Set 2", 11.0, R.drawable.dinner_set_2),
        MenuItems("Dinner Set 3", 11.0, R.drawable.dinner_set_3)
    )


}

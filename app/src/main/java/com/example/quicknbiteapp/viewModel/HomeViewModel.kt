package com.example.quicknbiteapp.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.model.Restaurant

class HomeViewModel : ViewModel() {

    // All restaurants
    private val _restaurants = listOf(
        Restaurant("fatburger", "FatBurger", "Jalan Burma", R.drawable.fatburger_recommend),
        Restaurant("layers_bakeshop", "Layer's Bakeshop", "Cafe Town", R.drawable.dessert),
        Restaurant("burger_heaven", "Burger Heaven", "Penang Street", R.drawable.food),
        Restaurant("cafe_mocha", "Cafe Mocha", "Gurney Drive", R.drawable.drink)
    )

    // Search query state
    var searchQuery by mutableStateOf("")
        private set

    // Filtered restaurants based on search query
    val filteredRestaurants by derivedStateOf {
        if (searchQuery.isEmpty()) _restaurants
        else _restaurants.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Function to update search query
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
}

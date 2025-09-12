package com.example.quicknbiteapp.ui.state

import com.example.quicknbiteapp.data.model.MenuItem
import com.example.quicknbiteapp.data.model.Order
import com.example.quicknbiteapp.data.model.Vendor

sealed interface VendorUiState {
    object Loading : VendorUiState
    data class Success(
        val vendor: Vendor, // Replace 'Vendor' with your actual Vendor data class
        val menuItems: List<MenuItem>, // Replace 'MenuItem' with your actual MenuItem data class
        val orders: List<Order> // Replace 'Order' with your actual Order data class
    ) : VendorUiState
    data class Error(val message: String) : VendorUiState
    object NotAuthenticated : VendorUiState // Added for clarity
    object Idle : VendorUiState // Optional: an initial state before loading starts
}
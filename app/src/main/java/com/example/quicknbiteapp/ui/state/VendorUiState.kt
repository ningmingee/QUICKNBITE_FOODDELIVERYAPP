package com.example.quicknbiteapp.ui.state

import com.example.quicknbiteapp.data.model.MenuItem
import com.example.quicknbiteapp.data.model.Order
import com.example.quicknbiteapp.data.model.Vendor

sealed interface VendorUiState {
    object Loading : VendorUiState
    data class Success(
        val vendor: Vendor,
        val menuItems: List<MenuItem>,
        val orders: List<Order>
    ) : VendorUiState
    data class Error(val message: String) : VendorUiState
    object NotAuthenticated : VendorUiState
    object Idle : VendorUiState
}
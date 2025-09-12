package com.example.quicknbiteapp.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknbiteapp.data.model.Customer
import com.example.quicknbiteapp.data.model.ProfileItem
import com.example.quicknbiteapp.repository.CustomerRepository
import com.example.quicknbiteapp.ui.state.LogoutState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel (
    private val authViewModel: AuthViewModel,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer.asStateFlow()

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCustomerData()
    }

    fun loadCustomerData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.uid?.let { customerId ->
                    val customerData = customerRepository.getCustomer(customerId)
                    _customer.value = customerData ?: createNewCustomer(customerId, currentUser)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun createNewCustomer(
        customerId: String,
        firebaseUser: com.google.firebase.auth.FirebaseUser
    ): Customer {
        val newCustomer = Customer(
            customerId = customerId,
            email = firebaseUser.email ?: "",
            fullName = firebaseUser.displayName ?: "Customer",
            phoneNumber = firebaseUser.phoneNumber ?: ""
        )

        customerRepository.createCustomer(newCustomer)
        return newCustomer
    }

    fun updateCustomerProfile(updates: Map<String, Any>) {
        viewModelScope.launch {
            val customerId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val success = customerRepository.updateCustomer(customerId, updates)
            if (success) {
                loadCustomerData() // Reload updated data
            }
        }
    }

    val profileOptions = listOf(
        ProfileItem(Icons.Default.Person, "Edit Profile") { editProfile() },
        ProfileItem(Icons.Default.ShoppingCart, "My Orders") { viewOrders() },
        ProfileItem(Icons.Default.Settings, "Settings") { openSettings() },
        ProfileItem(Icons.Default.Help, "Help / Support") { openHelp() },
        ProfileItem(Icons.Default.Logout, "Logout") { showLogoutConfirmation() }
    )

    private fun editProfile() { /* TODO: navigate to edit profile */ }
    private fun viewOrders() { /* TODO: navigate to my orders */ }
    private fun openSettings() { /* TODO: navigate to SettingsScreen */ }
    private fun openHelp() { /* TODO: navigate to Help / Support */ }

    fun showLogoutConfirmation() {
        _showLogoutDialog.value = true
    }

    fun dismissLogoutConfirmation() {
        _showLogoutDialog.value = false
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            try {
                authViewModel.signOut()
                _logoutState.value = LogoutState.Success
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error(e.message ?: "Logout failed")
            }
        }
    }

    fun resetLogoutState() {
        _logoutState.value = LogoutState.Idle
    }
}
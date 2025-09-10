package com.example.quicknbiteapp.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknbiteapp.data.model.ProfileItem
import com.example.quicknbiteapp.data.model.ProfileUser
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.ui.state.LogoutState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog

    val user = ProfileUser(
        name = "Ooi Mei Yi",
        email = "ooimeiyi@gmail.com",
        profileImageRes = R.drawable.profile
    )

    val profileOptions = listOf(
        ProfileItem(Icons.Default.Person, "Edit Profile") { editProfile() },
        ProfileItem(Icons.Default.ShoppingCart, "My Orders") { viewOrders() },
        ProfileItem(Icons.Default.Settings, "Settings") { openSettings() },
        ProfileItem(Icons.Default.Help, "Help / Support") { openHelp() },
        ProfileItem(Icons.Default.Logout, "Logout") { logout() }
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
                // Simulate logout process
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


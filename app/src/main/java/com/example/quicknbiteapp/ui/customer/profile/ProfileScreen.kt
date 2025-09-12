package com.example.quicknbiteapp.ui.customer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.data.model.ProfileItem
import com.example.quicknbiteapp.viewModel.ProfileViewModel
import com.example.quicknbiteapp.ui.state.LogoutState
import com.example.quicknbiteapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val customer by profileViewModel.customer.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val logoutState by profileViewModel.logoutState.collectAsState()
    val showLogoutDialog by profileViewModel.showLogoutDialog.collectAsState()

    // Handle logout state changes - FIXED: Moved to top level
    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            profileViewModel.resetLogoutState()
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile_title),
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simple Profile Icon (No image upload needed)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                customer?.getDisplayName() ?: "Loading...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                customer?.email ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Display loyalty points if available
            customer?.loyaltyPoints?.takeIf { it > 0 }?.let { points ->
                Spacer(Modifier.height(8.dp))
                Text(
                    "$points Loyalty Points",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(24.dp))

            val options = profileViewModel.profileOptions.map { item ->
                when (item.title) {
                    "Settings" -> item.copy(action = { navController.navigate("settings") })
                    "Logout" -> item.copy(action = { profileViewModel.showLogoutConfirmation() })
                    else -> item
                }
            }

            options.forEach { ProfileOption(it) }
        }

        // Logout Confirmation Dialog - FIXED: Better placement
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { profileViewModel.dismissLogoutConfirmation() },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    Button(
                        onClick = { profileViewModel.logout() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { profileViewModel.dismissLogoutConfirmation() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileOption(item: ProfileItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { item.action() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                item.icon,
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                item.title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
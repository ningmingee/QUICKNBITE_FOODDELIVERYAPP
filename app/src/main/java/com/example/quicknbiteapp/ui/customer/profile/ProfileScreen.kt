package com.example.quicknbiteapp.ui.customer.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    val user = profileViewModel.user
    val logoutState by profileViewModel.logoutState.collectAsState()
    val showLogoutDialog by profileViewModel.showLogoutDialog.collectAsState()

    // Handle logout state changes
    LaunchedEffect(logoutState) {
        when (logoutState) {
            is LogoutState.Success -> {
                // Navigate after successful logout
                profileViewModel.resetLogoutState()
                onLogout()
            }
            is LogoutState.Error -> {
                // You could show an error message here
                profileViewModel.resetLogoutState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile_title),
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Show loading overlay during logout
            if (logoutState is LogoutState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile image + info
                Image(
                    painter = painterResource(id = user.profileImageRes),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Spacer(Modifier.height(12.dp))
                Text(
                    user.name,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    user.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))

                // Map options with navigation actions
                val options = profileViewModel.profileOptions.map { item ->
                    when (item.title) {
                        "Settings" -> item.copy(action = { navController.navigate("settings") })
                        "Logout" -> item.copy(action = { profileViewModel.logout() })
                        else -> item
                    }
                }

                options.forEach { ProfileOption(it) }
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { profileViewModel.dismissLogoutConfirmation() },
                    title = {
                        Text(
                            text = stringResource(R.string.logout_dialog_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(stringResource(R.string.logout_confirmation))
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                profileViewModel.logout()
                                profileViewModel.dismissLogoutConfirmation()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.logout_button))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { profileViewModel.dismissLogoutConfirmation() }
                        ) {
                            Text(stringResource(R.string.cancel_button))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileOption(item: ProfileItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
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
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                item.title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
package com.example.quicknbiteapp.ui.vendor

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.ui.vendor.components.ProfileImagePicker
import com.example.quicknbiteapp.viewModel.AuthViewModel
import com.example.quicknbiteapp.viewModel.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorSettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    vendorViewModel: VendorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val vendorSettings by vendorViewModel.vendorSettings.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }
    val isLoading by vendorViewModel.isLoading.collectAsState()

    // Load settings when screen appears
    LaunchedEffect(Unit) {
        vendorViewModel.loadVendorSettings()
    }

    val pushNotificationsEnabled = vendorSettings?.pushNotifications ?: true
    val emailNotificationsEnabled = vendorSettings?.emailNotifications ?: true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Profile Header
            ProfileHeaderSection(
                businessName = vendorSettings?.businessName ?: "Business Name",
                email = currentUser?.email ?: "No email",
                profileImageUrl = vendorSettings?.profileImageUrl ?: "",
                onImageSelected = { uri ->
                    vendorViewModel.uploadProfileImage(uri)
                },
                navController = navController
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Business Information Section
            SettingsSection(title = "Business Information") {
                SettingsItem(
                    icon = Icons.Default.Store,
                    title = "Business Profile",
                    subtitle = vendorSettings?.businessName ?: "Not set",
                    onClick = { navController.navigate("business_profile") }
                )
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "Operating Hours",
                    subtitle = vendorSettings?.operatingHours?.takeIf { it.isNotBlank() } ?: "Not set",
                    onClick = { navController.navigate("operating_hours") }
                )
                SettingsItem(
                    icon = Icons.Default.Business,
                    title = "Business Address",
                    subtitle = vendorSettings?.businessAddress ?: "Not set",
                    onClick = { navController.navigate("business_address") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Settings Section
            SettingsSection(title = "Account Settings") {
                SettingsItem(
                    icon = Icons.Default.AccountCircle,
                    title = "Account Information",
                    subtitle = currentUser?.displayName ?: "Update your details",
                    onClick = { navController.navigate("account_info") }
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your password",
                    onClick = { navController.navigate("change_password") }
                )
                SettingsItem(
                    icon = Icons.Default.Payment,
                    title = "Payment Methods",
                    subtitle = "Manage payment options",
                    onClick = { navController.navigate("payment_methods") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications Section
            SettingsSection(title = "Notifications") {
                SettingsItemWithSwitch(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    subtitle = "Order updates and alerts",
                    checked = pushNotificationsEnabled,
                    onCheckedChange = { enabled ->
                        vendorViewModel.updateNotificationSettings(
                            pushEnabled = enabled,
                            emailEnabled = emailNotificationsEnabled
                        )
                    }
                )
                SettingsItemWithSwitch(
                    icon = Icons.Default.Email,
                    title = "Email Notifications",
                    subtitle = "Reports and summaries",
                    checked = emailNotificationsEnabled,
                    onCheckedChange = { enabled ->
                        vendorViewModel.updateNotificationSettings(
                            pushEnabled = pushNotificationsEnabled,
                            emailEnabled = enabled
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Support Section
            SettingsSection(title = "Support & About") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Help & Support",
                    subtitle = "Get help with your account",
                    onClick = { navController.navigate("help_support") }
                )
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About Quick & Bite",
                    subtitle = "App version 1.0.0",
                    onClick = { navController.navigate("about_app") }
                )
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    onClick = { navController.navigate("privacy_policy") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Button
            OutlinedButton(
                onClick = { showSignOutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign out",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.sign_out),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Version
            Text(
                text = "App Version 1.0.0",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }

        // Sign Out Confirmation Dialog
        if (showSignOutDialog) {
            SignOutConfirmationDialog(
                onConfirm = {
                    authViewModel.signOut()
                    navController.navigate("userTypeSelect") {
                        popUpTo(0)
                    }
                    showSignOutDialog = false
                },
                onDismiss = { showSignOutDialog = false }
            )
        }
    }
}

@Composable
fun ProfileHeaderSection(
    businessName: String,
    email: String,
    profileImageUrl: String = "",
    onImageSelected: (Uri) -> Unit = {},
    isLoading: Boolean = false,
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
)  {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            ProfileImagePicker(
                imageUrl = profileImageUrl,
                onImageSelected = onImageSelected,
                isLoading = isLoading,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Business Name
            Text(
                text = businessName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Profile Button
            OutlinedButton(
                onClick = { navController?.navigate("edit_profile") },
                shape = RoundedCornerShape(20.dp),
                enabled = !isLoading
            ) {
                Text("Edit Profile")
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun SignOutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to sign out?")
        },
        confirmButton = {
            OutlinedButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Sign Out")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
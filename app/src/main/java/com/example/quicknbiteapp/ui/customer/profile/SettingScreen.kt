package com.example.quicknbiteapp.ui.customer.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.quicknbiteapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkTheme by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    )  { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Notifications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .toggleable(
                        value = notificationsEnabled,
                        onValueChange = { notificationsEnabled = it }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.notifications_setting),
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            // Address Book
            SettingsOption(Icons.Default.LocationOn, stringResource(R.string.address_book)) { /* navigate */ }

            // Payment Preferences
            SettingsOption(Icons.Default.CreditCard, stringResource(R.string.payment_preferences)) { /* navigate */ }

            // Language
            SettingsOption(Icons.Default.Language, stringResource(R.string.language, language)) { /* select language */ }

            // Dark Theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .toggleable(
                        value = darkTheme,
                        onValueChange = { darkTheme = it }
                    ),
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(
                    stringResource(R.string.dark_theme),
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = darkTheme,
                    onCheckedChange = { darkTheme = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            // Privacy / Terms
            SettingsOption(Icons.Default.Description, stringResource(R.string.privacy_terms)) { /* navigate */ }

            // App Version
            SettingsOption(Icons.Default.Info, stringResource(R.string.app_version, "1.0.0")) { /* info */ }
        }
    }
}

@Composable
fun SettingsOption(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, action: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { action() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text(
                title,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
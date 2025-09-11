package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.ui.vendor.components.ProfileImagePicker
import com.example.quicknbiteapp.viewModel.AuthViewModel
import com.example.quicknbiteapp.viewModel.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    vendorViewModel: VendorViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val vendorSettings by vendorViewModel.vendorSettings.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val profileImageUri by vendorViewModel.profileImageUri.collectAsState()

    var businessName by remember { mutableStateOf(vendorSettings?.businessName ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(vendorSettings?.phoneNumber ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Image Section
            Text(
                text = "Profile Photo",
                style = MaterialTheme.typography.titleMedium
            )

            ProfileImagePicker(
                imageUri = profileImageUri,
                onImageSelected = { uri ->
                    vendorViewModel.setProfileImage(uri)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = { vendorViewModel.clearProfileImage() },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Remove Photo")
            }
            OutlinedTextField(
                value = businessName,
                onValueChange = { businessName = it },
                label = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Email shouldn't be editable here
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vendorViewModel.updateAccountInfo(
                        displayName = businessName,
                        phoneNumber = phoneNumber
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
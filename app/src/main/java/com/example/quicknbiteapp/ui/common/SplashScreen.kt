package com.example.quicknbiteapp.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.viewModel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val userType by authViewModel.userType.collectAsState()

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()

        if (currentUser == null || userType == null) {
            delay(2000) // Minimum 2 seconds delay splash screen time
        }

        when {
            currentUser == null || userType == null -> {
                // Ensure we show splash for at least 2 seconds
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < 2000) {
                    delay(2000 - elapsed)
                }
                navController.navigate("userTypeSelect") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {
                when (userType) {
                    "vendor" -> {
                        navController.navigate("vendor_main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }

                    "customer" -> {
                        navController.navigate("customer_main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate("userTypeSelect") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo/icon
            Image(
                painter = painterResource(id = R.drawable.logoicon),
                contentDescription = "Quick&Bite Logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}


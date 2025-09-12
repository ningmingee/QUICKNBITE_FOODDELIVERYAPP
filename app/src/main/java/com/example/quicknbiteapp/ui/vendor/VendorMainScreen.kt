package com.example.quicknbiteapp.ui.vendor

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.viewModel.AuthViewModel
import com.example.quicknbiteapp.viewModel.VendorViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VendorMainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    vendorViewModel: VendorViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            VendorBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> VendorDashboardScreen(navController = navController, viewModel = vendorViewModel)
                1 -> VendorOrdersScreen(navController = navController, viewModel = vendorViewModel)
                2 -> VendorMenuScreen(navController = navController)
                3 -> VendorSettingsScreen(navController, authViewModel)
            }
        }
    }
}

@Composable
fun VendorBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dashboard),
                    contentDescription = stringResource(id = R.string.dashboard_title),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(id = R.string.dashboard_title)) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_orders),
                    contentDescription = stringResource(id = R.string.orders),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(id = R.string.orders)) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = stringResource(id = R.string.menu_title),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(id = R.string.menu_title)) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = stringResource(id = R.string.settings),
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text(stringResource(id = R.string.settings)) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}
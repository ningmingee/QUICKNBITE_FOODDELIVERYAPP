package com.example.quicknbiteapp.ui.customer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.ui.customer.activity.ActivityScreen
import com.example.quicknbiteapp.ui.customer.chat.MessageScreen
import com.example.quicknbiteapp.ui.customer.game.GameScreen
import com.example.quicknbiteapp.ui.customer.home.HomeScreen
import com.example.quicknbiteapp.ui.customer.profile.ProfileScreen
import com.example.quicknbiteapp.viewModel.CartViewModel
import com.example.quicknbiteapp.viewModel.ChatViewModel
import com.example.quicknbiteapp.viewModel.ProfileViewModel
import com.example.quicknbiteapp.viewModel.HomeViewModel


sealed class CustomerBottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : CustomerBottomNavItem("home", "Home", Icons.Default.Home)
    object Activity : CustomerBottomNavItem("activity", "Activity", Icons.Default.List)
    object Game : CustomerBottomNavItem("game", "Game", Icons.Default.SportsEsports)
    object Message : CustomerBottomNavItem("message", "Message", Icons.Default.Email)
    object Profile : CustomerBottomNavItem("profile", "Profile", Icons.Default.Person)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomerMainScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        CustomerBottomNavItem.Home,
        CustomerBottomNavItem.Activity,
        CustomerBottomNavItem.Game,
        CustomerBottomNavItem.Message,
        CustomerBottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            CustomerBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                items = bottomNavItems
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    cartViewModel = cartViewModel,
                    homeViewModel = homeViewModel
                )
                1 -> ActivityScreen(cartViewModel = cartViewModel)
                2 -> GameScreen(
                    cartViewModel = cartViewModel,
                    onGameEnd = {
                        // Navigate back to activity screen after game ends
                        selectedTab = 1
                    }
                )
                3 -> MessageScreen(chatViewModel = chatViewModel)
                4 -> ProfileScreen(
                    profileViewModel = profileViewModel,
                    navController = navController,
                    onLogout = {
                        // Navigate back to login screen when logout is successful
                        navController.navigate("userTypeSelect") {
                            popUpTo(0) // Clear back stack
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    items: List<CustomerBottomNavItem>
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}


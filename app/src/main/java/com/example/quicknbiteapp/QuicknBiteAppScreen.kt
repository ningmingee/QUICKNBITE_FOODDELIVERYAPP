package com.example.quicknbiteapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quicknbiteapp.ui.auth.CustomerLoginOrSignUpScreen
import com.example.quicknbiteapp.ui.auth.ForgotPasswordScreen
import com.example.quicknbiteapp.ui.auth.VendorLoginOrSignUpScreen
import com.example.quicknbiteapp.ui.common.SplashScreen
import com.example.quicknbiteapp.ui.common.UserTypeSelectScreen
import com.example.quicknbiteapp.ui.customer.CategoryItem.DessertScreen
import com.example.quicknbiteapp.ui.customer.CategoryItem.DrinksScreen
import com.example.quicknbiteapp.ui.customer.CategoryItem.FoodScreen
import com.example.quicknbiteapp.ui.customer.CategoryItem.MealPlansScreen
import com.example.quicknbiteapp.ui.customer.CategoryItem.OffersScreen
import com.example.quicknbiteapp.ui.customer.CustomerMainScreen
import com.example.quicknbiteapp.ui.customer.cart.CartScreen
import com.example.quicknbiteapp.ui.customer.cart.CheckoutScreen
import com.example.quicknbiteapp.ui.customer.discountOrder.DiscountOrderScreen
import com.example.quicknbiteapp.ui.customer.food.FatBurgerScreen
import com.example.quicknbiteapp.ui.customer.food.LayersBakeshopScreen
import com.example.quicknbiteapp.ui.customer.orderTracking.OrderTrackingScreen
import com.example.quicknbiteapp.ui.customer.profile.ProfileScreen
import com.example.quicknbiteapp.ui.customer.profile.SettingsScreen
import com.example.quicknbiteapp.ui.theme.QUICKNBITETheme
import com.example.quicknbiteapp.ui.vendor.AboutAppScreen
import com.example.quicknbiteapp.ui.vendor.AccountInfoScreen
import com.example.quicknbiteapp.ui.vendor.BusinessAddressScreen
import com.example.quicknbiteapp.ui.vendor.BusinessProfileScreen
import com.example.quicknbiteapp.ui.vendor.ChangePasswordScreen
import com.example.quicknbiteapp.ui.vendor.EditProfileScreen
import com.example.quicknbiteapp.ui.vendor.HelpSupportScreen
import com.example.quicknbiteapp.ui.vendor.OperatingHoursScreen
import com.example.quicknbiteapp.ui.vendor.PaymentMethodsScreen
import com.example.quicknbiteapp.ui.vendor.PrivacyPolicyScreen
import com.example.quicknbiteapp.ui.vendor.VendorDashboardScreen
import com.example.quicknbiteapp.ui.vendor.VendorMainScreen
import com.example.quicknbiteapp.ui.vendor.VendorMenuScreen
import com.example.quicknbiteapp.ui.vendor.VendorOrderDetailScreen
import com.example.quicknbiteapp.ui.vendor.VendorOrdersScreen
import com.example.quicknbiteapp.ui.vendor.VendorReviewsScreen
import com.example.quicknbiteapp.ui.vendor.VendorSettingsScreen
import com.example.quicknbiteapp.utils.FacebookLoginHelper
import com.example.quicknbiteapp.viewModel.AuthViewModel
import com.example.quicknbiteapp.viewModel.CartViewModel
import com.example.quicknbiteapp.viewModel.ChatViewModel
import com.example.quicknbiteapp.viewModel.ProfileViewModel
import com.example.quicknbiteapp.viewModel.VendorViewModel
import com.example.quicknbiteapp.viewModel.HomeViewModel

@Composable
fun QuicknBiteAppScreen() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val vendorViewModel: VendorViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel { ProfileViewModel(authViewModel) }
    val homeViewModel: HomeViewModel = viewModel()


    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Common screens
        composable("splash") {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("userTypeSelect") {
            UserTypeSelectScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController)
        }

        // Auth screens
        composable("customer_login") {
            CustomerLoginOrSignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
                isLoginMode = true,

            )
        }
        composable("customer_signup") {
            CustomerLoginOrSignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
                isLoginMode = false
            )
        }
        composable("vendor_login") {
            VendorLoginOrSignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
                isLoginMode = true
            )
        }
        composable("vendor_signup") {
            VendorLoginOrSignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
                isLoginMode = false
            )
        }

        // Vendor flow
        composable("vendor_main") {
            VendorMainScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("vendor_dashboard") {
            VendorDashboardScreen(
                viewModel = vendorViewModel,
                navController = navController
            )
        }
        composable("vendor_orders") {
            VendorOrdersScreen(
                navController = navController,
                viewModel = vendorViewModel
            )
        }
        composable(
            "vendor_order_detail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            VendorOrderDetailScreen(
                navController = navController,
                orderId = orderId,
                viewModel = vendorViewModel
            )
        }
        composable("vendor/reviews") {
            VendorReviewsScreen(
                viewModel = vendorViewModel,
                navController = navController
            )
        }
        composable("vendor_menu") {
            VendorMenuScreen(navController = navController)
        }
        composable("vendor_settings") {
            VendorSettingsScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("edit_profile") {
            EditProfileScreen(navController = navController, vendorViewModel = vendorViewModel, authViewModel = authViewModel)
        }
        composable("business_profile") {
            BusinessProfileScreen(viewModel = vendorViewModel, navController = navController)
        }
        composable("operating_hours") {
            OperatingHoursScreen(viewModel = vendorViewModel, navController = navController)
        }
        composable("business_address") {
            BusinessAddressScreen(viewModel = vendorViewModel, navController = navController)
        }
        composable("account_info") {
            AccountInfoScreen(viewModel = vendorViewModel, navController = navController)
        }
        composable("payment_methods") {
            PaymentMethodsScreen(navController = navController)
        }
        composable("help_support") {
            HelpSupportScreen(navController = navController)
        }
        composable("about_app") {
            AboutAppScreen(navController = navController)
        }
        composable("privacy_policy") {
            PrivacyPolicyScreen(navController = navController)
        }
        composable("change_password") {
            ChangePasswordScreen(authViewModel = authViewModel, navController = navController)
        }

        // Customer flow - Main container
        composable("customer_main") {
            CustomerMainScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                chatViewModel = chatViewModel,
                profileViewModel = profileViewModel,
                homeViewModel = homeViewModel
            )
        }

        // Customer flow - Individual screens (accessible from HomeScreen)
        composable("offers") {
            OffersScreen(
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }
        composable("drinks") {
            DrinksScreen(
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }
        composable("food") {
            FoodScreen(
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }
        composable("dessert") {
            DessertScreen(
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }
        composable("mealplans") {
            MealPlansScreen(
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }
        composable("order") {
            DiscountOrderScreen(
                onBack = { navController.popBackStack() },
                cartViewModel = cartViewModel
            )
        }
        composable("fatburger") {
            FatBurgerScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }
        composable("layers_bakeshop") {
            LayersBakeshopScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onCartClick = { navController.navigate("cart") }
            )
        }
        composable("cart") {
            CartScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate("checkout") },
                onAddItem = {
                    navController.navigate("fatburger") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("checkout") {
            CheckoutScreen(
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate("orderTracking") {
                        popUpTo("cart") { inclusive = true }
                    }
                    cartViewModel.clearCart()
                },
                onAddItem = {
                    navController.navigate("fatburger") {
                        launchSingleTop = true
                    }
                },
                onEditItem = { index ->
                    navController.navigate("cart") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("orderTracking") {
            OrderTrackingScreen(
                onBack = { navController.popBackStack() }
            )
        }
        // Profile with navController for Settings
        composable("profile") {
            ProfileScreen(
                profileViewModel = profileViewModel,
                navController = navController,
                onLogout = {
                    // Navigate back to login/splash screen
                    navController.navigate("userTypeSelect") {
                        popUpTo(0) // Clear back stack
                    }
                }
            )
        }

        // Settings Screen
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QUICKNBITEScreenPreview() {
    QUICKNBITETheme {
        QuicknBiteAppScreen()
    }
}
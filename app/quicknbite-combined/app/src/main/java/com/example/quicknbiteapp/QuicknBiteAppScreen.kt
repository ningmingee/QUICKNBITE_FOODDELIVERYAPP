package com.example.quicknbiteapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quicknbiteapp.viewmodel.AuthViewModel
import com.example.quicknbiteapp.ui.auth.CustomerLoginOrSignUpScreen
import com.example.quicknbiteapp.ui.auth.ForgotPasswordScreen
import com.example.quicknbiteapp.ui.common.SplashScreen
import com.example.quicknbiteapp.ui.common.UserTypeSelectScreen
import com.example.quicknbiteapp.ui.vendor.VendorDashboardScreen
import com.example.quicknbiteapp.ui.auth.VendorLoginOrSignUpScreen
import com.example.quicknbiteapp.ui.vendor.VendorMainScreen
import com.example.quicknbiteapp.ui.vendor.VendorMenuScreen
import com.example.quicknbiteapp.ui.vendor.VendorOrdersScreen
import com.example.quicknbiteapp.ui.vendor.VendorSettingsScreen
import com.example.quicknbiteapp.ui.theme.QuicknBiteAppTheme
import com.example.quicknbiteapp.ui.vendor.AccountInfoScreen
import com.example.quicknbiteapp.ui.vendor.BusinessAddressScreen
import com.example.quicknbiteapp.ui.vendor.BusinessProfileScreen
import com.example.quicknbiteapp.ui.vendor.ChangePasswordScreen
import com.example.quicknbiteapp.ui.vendor.OperatingHoursScreen
import com.example.quicknbiteapp.ui.vendor.VendorOrderDetailScreen
import com.example.quicknbiteapp.ui.vendor.VendorReviewsScreen
import com.example.quicknbiteapp.viewmodel.VendorViewModel

@Composable
fun QuicknBiteAppScreen() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val vendorViewModel: VendorViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("userTypeSelect") {
            UserTypeSelectScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("customer_login") {
            CustomerLoginOrSignUpScreen(
                navController = navController,
                authViewModel = authViewModel,
                isLoginMode = true
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
            VendorReviewsScreen(viewModel = vendorViewModel)
        }

        composable("vendor_menu") {
            VendorMenuScreen(navController = navController)
        }
        composable("vendor_settings") {
            VendorSettingsScreen(navController = navController, authViewModel = authViewModel)
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
        composable("change_password") {
            ChangePasswordScreen(authViewModel = authViewModel, navController = navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun QuicknBiteAppScreenPreview() {
    QuicknBiteAppTheme {
        QuicknBiteAppScreen()
    }
}





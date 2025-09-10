package com.example.quicknbiteapp.ui.common

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.UserTypeSelectState
import com.example.quicknbiteapp.viewModel.AuthViewModel
import com.example.quicknbiteapp.ui.customer.PrivacyPolicyScreen
import com.example.quicknbiteapp.ui.customer.TermsConditionsScreen
import com.example.quicknbiteapp.ui.theme.QUICKNBITETheme
import com.example.quicknbiteapp.ui.vendor.VendorAgreementScreen
import com.example.quicknbiteapp.utils.FacebookLoginHelper
import com.example.quicknbiteapp.utils.rememberFacebookLoginHelper
import com.example.quicknbiteapp.utils.rememberGoogleSignInHelper

@Composable
fun UserTypeSelectScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel? = null,
    facebookLoginHelper: FacebookLoginHelper? = null
) {
    var isCustomerSide by remember { mutableStateOf(true) }

    // Create Google Sign-In helper
    val googleSignInHelper = rememberGoogleSignInHelper()

    // Create the launcher for Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            googleSignInHelper.handleSignInResult(
                data = data,
                onSuccess = { idToken ->
                    // Pass the user type and token to ViewModel
                    val userType = "customer"
                    authViewModel?.signInWithGoogle(idToken, userType)
                },
                onError = { exception ->
                    authViewModel?.resetAuthState()
                    Log.e("GoogleSignIn", "Error: ${exception.message}")
                }
            )
        }
    }

    val context = LocalContext.current
    val activity = (context as? Activity)
    val facebookLoginHelper = rememberFacebookLoginHelper()

    // Setup Facebook callback
    LaunchedEffect(facebookLoginHelper) {
        facebookLoginHelper.setupCallback(
            onSuccess = { token ->
                val userType = "customer"
                authViewModel?.signInWithFacebook(token, userType)
                navController.navigate("customer_login")
            },
            onError = { error ->
                authViewModel?.resetAuthState()
                Log.e("FacebookSignIn", "Error: ${error.message}")
            },
            onCancel = {
                authViewModel?.resetAuthState()
                Log.d("FacebookSignIn", "Login cancelled")
            }
        )
    }


    // Observe authentication state - use remember to avoid recreating the state
    val authState by authViewModel?.authState?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(AuthViewModel.AuthState.Idle) }

    // Handle authentication state changes
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            val successState = authState as AuthViewModel.AuthState.Success
            // Navigate based on user type
            val destination = when (successState.userType) {
                "customer" -> "customer_main"
                "vendor" -> "vendor_main"
                else -> "customer_main"
            }
            navController.navigate(destination) {
                popUpTo("user_type_select") { inclusive = true }
            }
        }
    }

    val uiState by authViewModel?.uiState?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(UserTypeSelectState.MainScreen) }

    // Handle UI state changes
    when (uiState) {
        UserTypeSelectState.TermsConditions -> {
            TermsConditionsScreen(onBack = { authViewModel?.navigateBack() })
            return
        }
        UserTypeSelectState.PrivacyPolicy -> {
            PrivacyPolicyScreen(onBack = { authViewModel?.navigateBack() })
            return
        }
        UserTypeSelectState.VendorAgreement -> {
            VendorAgreementScreen(onBack = { authViewModel?.navigateBack() })
            return
        }
        UserTypeSelectState.MainScreen -> {
            // Continue with main screen content
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoicon),
                    contentDescription = "Quick&Bite Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(2.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stringResource(R.string.welcome_message),
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom card section
            Box(
                modifier = Modifier
                    .height(600.dp)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    // User type toggle
                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .width(200.dp)
                            .size(40.dp)
                            .height(20.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Customer button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(
                                    if (isCustomerSide) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { isCustomerSide = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.customer_button),
                                color = if (isCustomerSide) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Vendor button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(
                                    if (!isCustomerSide) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { isCustomerSide = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.vendor_button),
                                color = if (!isCustomerSide) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    // Action buttons
                    Button(
                        onClick = {
                            // Navigate to login screen
                            val destination = if (isCustomerSide) "customer_login" else "vendor_login"
                            navController.navigate(destination)
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .width(200.dp)
                            .size(40.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.login),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            val destination = if (isCustomerSide) "customer_signup" else "vendor_signup"
                            navController.navigate(destination)
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .width(200.dp)
                            .size(40.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.signup)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    if (isCustomerSide) {
                        // Divider with "or" text
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                thickness = DividerDefaults.Thickness,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            Text(
                                text = stringResource(R.string.or_sign_in_with),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                thickness = DividerDefaults.Thickness,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Box (
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ){
                            // Social Platform login buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                // Google button
                                OutlinedButton(
                                    onClick = {
                                        // Get the activity context
                                        val signInIntent = googleSignInHelper.getSignInIntent()
                                        googleSignInLauncher.launch(signInIntent)
                                    },
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.google),
                                        contentDescription = "Google logo",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        facebookLoginHelper.signIn(context as Activity)
                                        if (isCustomerSide && activity != null) {
                                            facebookLoginHelper.signIn(activity)
                                        }
                                    },
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.facebook),
                                        contentDescription = "Facebook logo",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Terms and privacy
                    if (isCustomerSide) {
                        // Customer terms
                        Text(
                            text = stringResource(R.string.agree_to_terms_and_privacy),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.terms_conditions),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    // Navigate to terms and conditions
                                    authViewModel?.showTermsConditions()
                                }
                            )
                            Text(
                                text = " ${stringResource(R.string.and)} ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.privacy_policy),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    // Navigate to privacy policy
                                    authViewModel?.showPrivacyPolicy()
                                }
                            )
                        }
                    } else {
                        // Vendor terms
                        Text(
                            text = stringResource(R.string.vendor_agree_business_terms),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = stringResource(R.string.view_vendor_agreement),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                // Navigate to vendor agreement
                                authViewModel?.showVendorAgreement()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserTypeSelectPreview() {
    QUICKNBITETheme {
        val navController = rememberNavController()
        UserTypeSelectScreen(navController = navController)
    }
}
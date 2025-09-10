package com.example.quicknbiteapp.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.viewmodel.AuthViewModel
import com.example.quicknbiteapp.ui.theme.QuicknBiteAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorLoginOrSignUpScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isLoginMode: Boolean = true
) {
    // Login state
    var businessEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Signup state
    var phoneNumber by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var businessAddress by remember { mutableStateOf("") }
    var operatingHours by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun resetSignupFields() {
        businessName = ""
        ownerName = ""
        phoneNumber = ""
        businessEmail = ""
        businessAddress = ""
        operatingHours = ""
        password = ""
        confirmPassword = ""
        agreeToTerms = false
    }

    val uiState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
            when (val currentState = uiState) {
                is AuthViewModel.AuthState.Loading -> {
                    isLoading = true
                }
                is AuthViewModel.AuthState.Success -> {
                    isLoading = false
                    if (currentState.isSignup) {
                        // For signup success, show message and navigate to login
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Signup successful! Please login.")
                        }
                        // Clear fields and switch to login mode
                        resetSignupFields()
                        navController.navigate("vendor_login?signupSuccess=true") {
                            popUpTo("vendor_signup") { inclusive = true }
                        }
                    } else {
                        // For login success, go to dashboard
                        navController.navigate("vendor_main") {
                            popUpTo(0)
                        }
                    }
                    authViewModel.resetAuthState()
                }
                is AuthViewModel.AuthState.Error -> {
                    isLoading = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(currentState.message)
                    }
                    authViewModel.resetAuthState()
                }
                AuthViewModel.AuthState.Idle -> {
                    isLoading = false
                }
                else -> {
                    isLoading = false
                }
            }

    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isLoginMode) "Vendor Login" else "Create Business Account",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLoginMode) 800.dp else 800.dp),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isLoginMode) stringResource(R.string.login_title)
                        else stringResource(R.string.create_business_account),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isLoginMode) stringResource(R.string.log_in_description)
                        else stringResource(R.string.vendor_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoginMode) {
                        // LOGIN FIELDS
                        OutlinedTextField(
                            value = businessEmail,
                            onValueChange = { businessEmail = it },
                            label = { Text(stringResource(R.string.business_email)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Hide password"
                                        else "Show password",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.forgot_password),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("forgot_password")
                                },
                            textAlign = TextAlign.End
                        )
                    } else {
                        // SIGNUP FIELDS
                        OutlinedTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = { Text(stringResource(R.string.business_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text(stringResource(R.string.owner_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text(stringResource(R.string.phone_number)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = businessEmail,
                            onValueChange = { businessEmail = it },
                            label = { Text(stringResource(R.string.business_email)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = businessAddress,
                            onValueChange = { businessAddress = it },
                            label = { Text(stringResource(R.string.business_address)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = operatingHours,
                            onValueChange = { operatingHours = it },
                            label = { Text(stringResource(R.string.operating_hours)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Hide password"
                                        else "Show password",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(R.string.confirm_password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            trailingIcon = {
                                val description = if (isConfirmPasswordVisible) "Hide password" else "Show password"
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                        contentDescription = description,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = agreeToTerms,
                                onCheckedChange = { agreeToTerms = it }
                            )
                            Text(
                                text = stringResource(R.string.agree_terms),
                                modifier = Modifier.clickable { agreeToTerms = !agreeToTerms },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login/Signup Button
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                if (businessEmail.isNotEmpty() && password.isNotEmpty()) {
                                    authViewModel.loginUser(businessEmail, password, "vendor")
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please fill in all fields")
                                    }
                                }
                            } else {
                                if (businessEmail.isNotEmpty() && password.isNotEmpty() &&
                                    confirmPassword.isNotEmpty() && agreeToTerms &&
                                    businessName.isNotEmpty() && ownerName.isNotEmpty() &&
                                    phoneNumber.isNotEmpty() && businessAddress.isNotEmpty() &&
                                    operatingHours.isNotEmpty()) {
                                    if (password == confirmPassword) {
                                        authViewModel.registerUser(
                                            email = businessEmail,
                                            password = password,
                                            userType = "vendor",
                                            userName = ownerName
                                        )
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Passwords don't match")
                                        }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please fill in all fields and agree to terms")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = if (isLoginMode) businessEmail.isNotEmpty() && password.isNotEmpty()
                        else businessEmail.isNotEmpty() && password.isNotEmpty() &&
                                confirmPassword.isNotEmpty() && agreeToTerms && !isLoading
                    ) {
                        Text(
                            text = if (isLoginMode) stringResource(R.string.log_in) else stringResource(R.string.sign_up),
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Switch between login and signup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLoginMode) stringResource(R.string.no_have_account)
                            else stringResource(R.string.already_have_account),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = if (isLoginMode) stringResource(R.string.sign_up)
                            else stringResource(R.string.log_in),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                if (isLoginMode) {
                                    navController.navigate("vendor_signup")
                                } else {
                                    resetSignupFields()
                                    navController.navigate("vendor_login")
                                }
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
fun VendorLoginOrSignUpScreenPreview() {
    QuicknBiteAppTheme {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()

        VendorLoginOrSignUpScreen(
            navController = navController,
            authViewModel = authViewModel,
            isLoginMode = true
        )
    }
}



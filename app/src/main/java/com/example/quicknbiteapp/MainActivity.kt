package com.example.quicknbiteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.quicknbiteapp.ui.theme.QUICKNBITETheme
import com.example.quicknbiteapp.viewModel.AuthViewModel
import com.example.quicknbiteapp.utils.FacebookLoginHelper

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var facebookLoginHelper: FacebookLoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel.initializeCredentialManager(this)
        facebookLoginHelper = FacebookLoginHelper(this)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            QUICKNBITETheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuicknBiteAppScreen()
                }
            }
        }
    }
}
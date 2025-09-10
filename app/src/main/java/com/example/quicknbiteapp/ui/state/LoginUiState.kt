package com.example.quicknbiteapp.ui.state

import com.google.firebase.auth.FirebaseUser

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: FirebaseUser) : LoginUiState
    data class Error(val message: String) : LoginUiState
}
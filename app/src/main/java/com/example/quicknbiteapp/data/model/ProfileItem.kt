package com.example.quicknbiteapp.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ProfileItem(
    val icon: ImageVector,
    val title: String,
    val action: () -> Unit
)
package com.example.quicknbiteapp.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class LocalMenuItem(
    @StringRes val name: Int,
    val price: String,
    val isAvailable: Boolean,
    @DrawableRes val imageResId: Int,
)
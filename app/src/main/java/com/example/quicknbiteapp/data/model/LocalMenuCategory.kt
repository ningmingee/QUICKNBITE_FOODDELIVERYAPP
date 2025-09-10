package com.example.quicknbiteapp.data.model

import androidx.annotation.StringRes

data class LocalMenuCategory(
    @StringRes val name: Int,
    val expanded: Boolean,
    val items: List<LocalMenuItem>
)

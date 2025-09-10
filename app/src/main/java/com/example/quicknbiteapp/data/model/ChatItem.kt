package com.example.quicknbiteapp.data.model

import androidx.compose.runtime.mutableStateListOf

data class ChatItem(
    val id: Int,
    val name: String,
    val subtitle: String,
    val lastMessage: String,
    val messages: MutableList<String> = mutableStateListOf()
)
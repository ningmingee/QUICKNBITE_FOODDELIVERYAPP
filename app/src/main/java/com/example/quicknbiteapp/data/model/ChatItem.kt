package com.example.quicknbiteapp.data.model

data class ChatItem(
    val id: Int,
    val name: String,
    val subtitle: String,
    val lastMessage: String,
    val messages: MutableList<String> = mutableListOf()
)
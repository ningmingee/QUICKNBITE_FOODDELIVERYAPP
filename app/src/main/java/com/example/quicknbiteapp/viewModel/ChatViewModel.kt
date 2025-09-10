package com.example.quicknbiteapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quicknbiteapp.data.model.ChatItem
import com.example.quicknbiteapp.data.model.NotificationItem

class ChatViewModel : ViewModel() {

    var selectedTab by mutableStateOf(0)   // 0 = Chats, 1 = Notifications
        private set

    var selectedChat by mutableStateOf<ChatItem?>(null)
        private set

    val chats = mutableStateListOf(
        ChatItem(1, "JOHN", "GrabFood Order • Mon", "This chat remains open for this order", mutableStateListOf("Hi John", "Your order is confirmed")),
        ChatItem(2, "Delivery Rider", "Order #456789", "Your food is on the way 🚴‍♂️", mutableStateListOf("On my way", "Arriving soon")),
        ChatItem(3, "Support", "Help Center", "How can we help you?", mutableStateListOf("Hello", "Need assistance?"))
    )

    val notifications = listOf(
        NotificationItem(1, "Promo: Get 20% off your next meal!!"),
        NotificationItem(2, "Reminder: Your breakfast order is scheduled for tomorrow"),
        NotificationItem(3, "System update completed successfully")
    )

    // Actions
    fun selectTab(tabIndex: Int) {
        selectedTab = tabIndex
    }

    fun selectChat(chat: ChatItem) {
        selectedChat = chat
    }

    fun backToList() {
        selectedChat = null
    }

    fun sendMessage(chat: ChatItem, message: String) {
        if (message.isNotBlank()) {
            chat.messages.add(message)
        }
    }
}

package com.example.quicknbiteapp.ui.customer.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quicknbiteapp.data.model.ChatItem
import com.example.quicknbiteapp.R

@Composable
fun ChatRow(chat: ChatItem, onClick: (ChatItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(chat) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChatAvatar()
        Spacer(modifier = Modifier.width(12.dp))
        ChatInfo(chat)
    }
}

@Composable
fun ChatAvatar() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoicon),
            contentDescription = "Avatar",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun ChatInfo(chat: ChatItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            chat.name,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            chat.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            chat.lastMessage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

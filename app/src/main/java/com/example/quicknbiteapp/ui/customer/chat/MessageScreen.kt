package com.example.quicknbiteapp.ui.customer.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.quicknbiteapp.viewModel.ChatViewModel
import com.example.quicknbiteapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(chatViewModel: ChatViewModel, modifier: Modifier = Modifier) {

    if (chatViewModel.selectedChat != null) {
        ChatDetailScreen(
            chat = chatViewModel.selectedChat!!,
            onBack = { chatViewModel.backToList() },
            onSend = { msg -> chatViewModel.sendMessage(chatViewModel.selectedChat!!, msg) }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.messages_title),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                TabRow(
                    selectedTabIndex = chatViewModel.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = chatViewModel.selectedTab == 0,
                        onClick = { chatViewModel.selectTab(0) },
                        text = {
                            Text(
                                stringResource(R.string.chats_tab),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                    Tab(
                        selected = chatViewModel.selectedTab == 1,
                        onClick = { chatViewModel.selectTab(1) },
                        text = {
                            Text(
                                stringResource(R.string.notifications_tab),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                when (chatViewModel.selectedTab) {
                    0 -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(chatViewModel.chats) { chat ->
                            ChatRow(chat) { chatViewModel.selectChat(it) }
                        }
                    }
                    1 -> NotificationList(chatViewModel.notifications)
                }
            }
        }
    }
}

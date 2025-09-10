package com.example.quicknbiteapp.ui.customer.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.res.stringResource
import com.example.quicknbiteapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(
    title: String,
    onCartClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = stringResource(R.string.cart_title),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

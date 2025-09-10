package com.example.user_fooddelivery.ui.discountOrder

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.user_fooddelivery.viewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountOrderScreen(
    onBack: () -> Unit,
    cartViewModel: CartViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Claim Discount / Order Screen",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Description
            Text(
                text = "This screen displays order details, discount offers, " +
                        "and allows users to claim them for their purchases."
            )
        }
    }
}

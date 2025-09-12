package com.example.quicknbiteapp.ui.customer.activity

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quicknbiteapp.viewModel.CartViewModel
import com.example.quicknbiteapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    cartViewModel: CartViewModel
) {
    val subtotalPoints = cartViewModel.getSubtotalPoints()
    val bonusPoints = cartViewModel.bonusPoints
    val totalPoints = cartViewModel.getTotalPoints()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.activity_title),
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,

                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.loyalty_points),
                fontSize = 22.sp,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Subtotal points
            Text(
                text = stringResource(R.string.points_from_purchases, subtotalPoints),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            // Bonus points
            Text(
                text = stringResource(R.string.bonus_points, bonusPoints),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Total points
            Text(
                text = stringResource(R.string.total_points, totalPoints),
                fontSize = 28.sp,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

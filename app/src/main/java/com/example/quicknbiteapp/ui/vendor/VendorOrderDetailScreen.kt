package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.data.model.Order
import com.example.quicknbiteapp.data.model.OrderItem
import com.example.quicknbiteapp.data.model.OrderStatus
import com.example.quicknbiteapp.ui.state.VendorUiState
import com.example.quicknbiteapp.viewModel.VendorViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrderDetailScreen(
    navController: NavHostController,
    orderId: String,
    viewModel: VendorViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val order = remember { derivedStateOf {
        (uiState as? VendorUiState.Success)
            ?.orders?.find { it.orderId == orderId }
    } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order #${orderId.take(15)}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        order.value?.let { currentOrder ->
            OrderDetailContent(
                order = currentOrder,
                onUpdateStatus = { newStatus ->
                    viewModel.updateOrderStatus(orderId, newStatus)
                },
                innerPadding = innerPadding
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Order not found")
            }
        }
    }
}

@Composable
fun OrderDetailContent(
    order: Order,
    onUpdateStatus: (OrderStatus) -> Unit,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Order Header
        OrderHeaderSection(order)

        Spacer(modifier = Modifier.height(24.dp))

        // Order Timeline
        OrderTimelineSection(order)

        Spacer(modifier = Modifier.height(24.dp))

        // Order Summary
        OrderSummarySection(order)

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons based on status
        OrderActionButtons(order, onUpdateStatus)
    }
}

@Composable
fun OrderHeaderSection(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order #${order.orderId.take(15)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
            Text(
                text = dateFormat.format(order.createdAt),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.status.name.replace("_", " "),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (order.status) {
                        OrderStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        OrderStatus.PREPARING -> MaterialTheme.colorScheme.primary
                        OrderStatus.READY_FOR_PICKUP -> MaterialTheme.colorScheme.tertiary
                        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Customer:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.customerName.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Phone:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = order.customerPhoneNumber.toString(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun OrderTimelineSection(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Timeline",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Timeline items - you can expand this with actual timestamps
            TimelineItem("11:00", "Order received", "Accepted in 10s")
            TimelineItem("11:00", "Order accepted", "")
            TimelineItem("11:00", "Rider near pick up", "")
            TimelineItem("11:00", "Order picked up", "")
            TimelineItem("11:00", "Order delivered", "")
        }
    }
}

@Composable
fun TimelineItem(time: String, event: String, description: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(60.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrderSummarySection(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Order items
            order.items.forEach { item ->
                OrderItemRow(item)
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(8.dp))

            // Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("RM ${order.totalAmount - 2.00}", color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Fee", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("RM 2.00", color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "RM ${order.totalAmount}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.quantity}x ${item.name}",
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "RM ${item.pricePerItem * item.quantity}",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun OrderActionButtons(
    order: Order,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    when (order.status) {
        OrderStatus.PENDING -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onUpdateStatus(OrderStatus.PREPARING) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Accept Order")
                }

                Button(
                    onClick = { onUpdateStatus(OrderStatus.CANCELLED) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reject Order")
                }
            }
        }
        OrderStatus.PREPARING -> {
            Button(
                onClick = { onUpdateStatus(OrderStatus.READY_FOR_PICKUP) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Mark as Ready for Pickup")
            }
        }
        OrderStatus.READY_FOR_PICKUP -> {
            Button(
                onClick = { onUpdateStatus(OrderStatus.COMPLETED) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Mark as Completed")
            }
        }
        else -> {
            // No action buttons for completed/cancelled orders
        }
    }
}
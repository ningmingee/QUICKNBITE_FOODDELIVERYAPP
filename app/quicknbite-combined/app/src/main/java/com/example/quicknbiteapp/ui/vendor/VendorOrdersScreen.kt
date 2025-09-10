package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.model.OrderStatus
import com.example.quicknbiteapp.viewmodel.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorOrdersScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: VendorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.orders),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is com.example.quicknbiteapp.ui.state.VendorUiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is com.example.quicknbiteapp.ui.state.VendorUiState.Success -> {
                OrdersContent(
                    navController = navController,
                    orders = state.orders,
                    onUpdateOrderStatus = { orderId, status ->
                        viewModel.updateOrderStatus(orderId, status)
                    },
                    innerPadding = innerPadding,
                    modifier = modifier
                )
            }
            is com.example.quicknbiteapp.ui.state.VendorUiState.Error -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${state.message}")
                        Button(
                            onClick = { viewModel.refreshData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun OrdersContent(
    navController: NavHostController,
    orders: List<com.example.quicknbiteapp.data.model.Order>,
    onUpdateOrderStatus: (String, OrderStatus) -> Unit,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Calculate order statistics
        val newOrders = orders.filter { it.status == OrderStatus.PENDING }
        val activeOrders = orders.filter {
            it.status == OrderStatus.PREPARING || it.status == OrderStatus.READY_FOR_PICKUP
        }
        val completedOrders = orders.filter { it.status == OrderStatus.COMPLETED }
        val todayOrders = orders.count { /* Add logic to filter today's orders */ true }

        // New Orders Section
        if (newOrders.isNotEmpty()) {
            Text(
                text = stringResource(R.string.new_orders),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            newOrders.forEach { order ->
                NewOrderCard(
                    order = order,
                    onAccept = {
                        onUpdateOrderStatus(order.orderId, OrderStatus.PREPARING)
                    },
                    onReject = {
                        onUpdateOrderStatus(order.orderId, OrderStatus.CANCELLED)
                    },
                    onOrderClick = {
                        navController.navigate("vendor_order_detail/${order.orderId}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Orders Overview
        OrdersOverviewSection(
            todayOrders = todayOrders,
            pendingOrders = newOrders.size,
            completedOrders = completedOrders.size
        )

        Spacer(modifier = Modifier.height(24.dp))


        // ACTIVE ORDERS SECTION (Orders that are accepted)
        if (activeOrders.isNotEmpty()) {
            Text(
                text = "Active Orders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeOrders) { order ->
                    ActiveOrderItem(
                        order = order,
                        onStatusChange = { newStatus ->
                            onUpdateOrderStatus(order.orderId, newStatus)
                        },
                        onOrderClick = {
                            navController.navigate("vendor_order_detail/${order.orderId}")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // TODAY'S COMPLETED ORDERS SECTION
        if (completedOrders.isNotEmpty()) {
            Text(
                text = "Today",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            completedOrders.take(5).forEach { order ->
                CompletedOrderItem(
                    order = order,
                    onOrderClick = {
                        navController.navigate("vendor_order_detail/${order.orderId}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NewOrderCard(
    order: com.example.quicknbiteapp.data.model.Order,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onOrderClick() },
        colors = CardDefaults.cardColors(
            containerColor = when (order.status) {
                OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text =  order.orderId.take(14),  // Show phone num
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Accept / Reject Orders
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(stringResource(R.string.accept))
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(stringResource(R.string.reject))
                }
            }
        }
    }
}

@Composable
fun OrdersOverviewSection(
    todayOrders: Int,
    pendingOrders: Int,
    completedOrders: Int
) {
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
                text = stringResource(R.string.orders_overview),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OrderStatCard(stringResource(R.string.today), "$todayOrders")
                OrderStatCard(stringResource(R.string.pending), "$pendingOrders")
                OrderStatCard(stringResource(R.string.completed), "$completedOrders")
            }
        }
    }
}

@Composable
fun OrderStatCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ActiveOrderItem(
    order: com.example.quicknbiteapp.data.model.Order,
    viewModel: VendorViewModel = viewModel(),
    onStatusChange: (OrderStatus) -> Unit,
    onOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onOrderClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order #${order.orderId.take(15)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Customer: ${order.customerName}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Total: RM${order.totalAmount}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val formattedDate = remember(order.createdAt) {
                viewModel.formatOrderDate(order.createdAt)
            }

            // Date and time
            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = if (formattedDate == "Invalid date") {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Status: ${order.status}",
                fontSize = 14.sp,
                color = when (order.status) {
                    OrderStatus.PENDING -> MaterialTheme.colorScheme.secondary
                    OrderStatus.PREPARING -> MaterialTheme.colorScheme.primary
                    OrderStatus.READY_FOR_PICKUP -> MaterialTheme.colorScheme.tertiary
                    OrderStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                    OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
                }
            )

            // Show appropriate buttons based on current status
            when (order.status) {
                OrderStatus.PREPARING -> {
                    Button(
                        onClick = { onStatusChange(OrderStatus.PREPARING) },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Preparing")
                    }
                }
                OrderStatus.READY_FOR_PICKUP -> {
                    Button(
                        onClick = { onStatusChange(OrderStatus.READY_FOR_PICKUP) },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Mark as Delivery")
                    }
                }
                OrderStatus.COMPLETED -> {
                    Button(
                        onClick = { onStatusChange(OrderStatus.COMPLETED) },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Complete Order")
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CompletedOrderItem(
    order: com.example.quicknbiteapp.data.model.Order,
    onOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "#${order.orderId.take(4).uppercase()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm", java.util.Locale.getDefault())
                Text(
                    text = dateFormat.format(order.createdAt),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "Completed: **:**",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
            )
        }
    }
}

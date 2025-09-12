package com.example.quicknbiteapp.ui.customer.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.model.DeliveryOption
import com.example.quicknbiteapp.utils.LocationManager
import com.example.quicknbiteapp.viewModel.CartViewModel


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onAddItem: () -> Unit,
    onEditItem: (index: Int) -> Unit
) {
    val summary = cartViewModel.getSummary()
    val selectedOption = cartViewModel.deliveryOption
    var selectedTime by remember { mutableStateOf(cartViewModel.deliveryTime) }
    var location by remember { mutableStateOf("") }

    // Redeem points
    val totalPoints = cartViewModel.getTotalPoints()
    var redeemPoints by remember { mutableStateOf(false) }
    val redeemDiscount = if (redeemPoints) totalPoints * 0.01 else 0.0
    val finalTotal = (summary.total - redeemDiscount).coerceAtLeast(0.0)

    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Generate time slots 9:00 AM - 9:00 PM every 30 min
    val timeSlots = remember {
        val slots = mutableListOf<String>()
        for (hour in 9 until 21) {
            for (minute in listOf(0, 30)) {
                if (hour == 20 && minute == 30) break
                val startHour12 = if (hour > 12) hour - 12 else hour
                val startAmPm = if (hour < 12) "AM" else "PM"
                val endHour = if (minute == 30) hour else hour
                val endMinute = if (minute == 30) 0 else 30
                val endHour12 = if (endHour > 12) endHour - 12 else endHour
                val endAmPm = if (endHour < 12) "AM" else "PM"
                val startTime = String.format("%d:%02d %s", startHour12, minute, startAmPm)
                val endTime = String.format("%d:%02d %s", endHour12, endMinute, endAmPm)
                slots.add("$startTime - $endTime")
            }
        }
        slots
    }

    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }

    // Step 1: Fetch GPS location when screen opens
    LaunchedEffect(Unit) {
        locationManager.fetchUserLocation()
    }

    // Step 2: Update deliveryLocation when GPS resolves
    LaunchedEffect(locationManager.userLocation.value) {
        locationManager.userLocation.value?.let { loc ->
            cartViewModel.deliveryLocation = loc.address
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.checkout_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Order Summary + Add Item
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.order_summary),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = onAddItem) {
                            Text(stringResource(R.string.add_item))
                        }
                    }
                }

                // Cart Items
                itemsIndexed(cartViewModel.cartItems) { index, item ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEditItem(index) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = stringResource(R.string.quantity, item.quantity),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = stringResource(R.string.edit),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { onEditItem(index) },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "RM ${"%.2f".format(item.getTotalPrice())}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                // Delivery Option
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.delivery_option),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        DeliveryOption.entries.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { cartViewModel.selectDeliveryOption(option) }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedOption == option,
                                    onClick = { cartViewModel.selectDeliveryOption(option) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    option.label,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Delivery Time Dropdown + Location
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.select_delivery_time),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp),
                            style = MaterialTheme.typography.titleMedium
                        )

                        var expanded by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { expanded = true }
                                .padding(12.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedTime.ifEmpty { stringResource(R.string.select_time_slot) },
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                                color = if (selectedTime.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_right),
                                contentDescription = "Dropdown Arrow",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            timeSlots.forEach { slot ->
                                DropdownMenuItem(
                                    text = { Text(text = slot, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = {
                                        selectedTime = slot
                                        cartViewModel.selectDeliveryTime(slot)
                                        expanded = false
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.delivery_location),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        OutlinedTextField(
                            value = cartViewModel.deliveryLocation,
                            onValueChange = { cartViewModel.deliveryLocation = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.enter_house_address),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                // Redeem Points Section
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.subtotal, summary.subtotal),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.tax, summary.tax),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.delivery_fee, summary.deliveryFee),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.redeem_points, totalPoints),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Switch(
                                checked = redeemPoints,
                                onCheckedChange = { redeemPoints = it },
                                enabled = totalPoints > 0,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }

                        if (redeemPoints && redeemDiscount > 0) {
                            Text(
                                text = stringResource(R.string.discount, redeemDiscount),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Payment Method Section
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.payment_method),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp),
                            style = MaterialTheme.typography.titleMedium
                        )

                        val paymentOptions = listOf(
                            stringResource(R.string.cash),
                            stringResource(R.string.online_banking),
                            stringResource(R.string.credit_debit_card)
                        )
                        var selectedPayment by remember { mutableStateOf(cartViewModel.paymentMethod) }

                        paymentOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPayment = option
                                        cartViewModel.selectPaymentMethod(option)
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedPayment == option,
                                    onClick = {
                                        selectedPayment = option
                                        cartViewModel.selectPaymentMethod(option)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(150.dp))
                }
            }

            // Bottom Bar: Total + Place Order
            if (cartViewModel.cartItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp + navBarPadding)
                ) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.total, finalTotal),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (redeemPoints) cartViewModel.resetBonusPoints()
                            onConfirm()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.place_order),
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

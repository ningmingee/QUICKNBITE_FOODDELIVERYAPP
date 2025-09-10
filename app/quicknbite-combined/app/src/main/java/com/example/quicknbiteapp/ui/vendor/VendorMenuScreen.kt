package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.model.LocalMenuCategory
import com.example.quicknbiteapp.data.model.LocalMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorMenuScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var menuCategories by remember {
        mutableStateOf(
            listOf(
                LocalMenuCategory(
                    name = R.string.burgers,
                    expanded = true,
                    items = listOf(
                        LocalMenuItem(
                            R.string.classic_burger,
                            "RM 6.00",
                            true,
                            R.drawable.classic_burger
                        ),
                        LocalMenuItem(
                            R.string.tripple_cheese_burger,
                            "RM 10.00",
                            true,
                            R.drawable.tripple_cheese_burger
                        ),
                        LocalMenuItem(
                            R.string.truffle_burger,
                            "RM 8.00",
                            true,
                            R.drawable.truffle_burger
                        ),
                        LocalMenuItem(
                            R.string.spicy_beast_burger,
                            "RM 12.00",
                            true,
                            R.drawable.spicy_beast_burger
                        ),
                    )
                ),
                LocalMenuCategory(
                    name = R.string.drinks,
                    expanded = true,
                    items = listOf(
                        LocalMenuItem(R.string._100_plus, "RM 3.50", true, R.drawable._100_plus),
                        LocalMenuItem(R.string.coca_cola, "RM 3.50", true, R.drawable.coca_cola),
                        LocalMenuItem(R.string.pepsi, "RM 3.50", true, R.drawable.pepsi),
                        LocalMenuItem(R.string.sour_plum_lemonade, "RM 5.00", true, R.drawable.sour_plum_lemonade),
                    )
                )
            )
        )
    }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var itemToToggle by remember { mutableStateOf<LocalMenuItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.menu_title),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Header - Fat Burger and Main Menu
                Text(
                    text = stringResource(R.string.fat_burger),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(R.string.main_menu),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Menu Categories with Dropdown
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(menuCategories) { category ->
                        MenuCategorySection(
                            category = category,
                            onToggleCategory = { categoryName ->
                                menuCategories = menuCategories.map { cat ->
                                    if (cat.name == categoryName) {
                                        cat.copy(expanded = !cat.expanded)
                                    } else {
                                        cat
                                    }
                                }
                            },
                            onToggleItemStatus = { item ->
                                itemToToggle = item
                                showConfirmationDialog = true
                            },
                            onEditItem = { /* Handle edit */ }
                        )
                    }
                }
            }

            // Confirmation Dialog
            if (showConfirmationDialog && itemToToggle != null) {
                AlertDialog(
                    onDismissRequest = {
                        showConfirmationDialog = false
                        itemToToggle = null
                    },
                    title = {
                        Text(
                            text = stringResource(itemToToggle?.name ?: R.string.classic_burger),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = stringResource(
                                    R.string.confirm_turn_off,
                                    stringResource(itemToToggle?.name ?: R.string.classic_burger)
                                ),
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.confirm_unavailable),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                itemToToggle?.let { item ->
                                    menuCategories = menuCategories.map { category ->
                                        val updatedItems = category.items.map { menuItem ->
                                            if (menuItem.name == item.name) {
                                                menuItem.copy(isAvailable = false)
                                            } else {
                                                menuItem
                                            }
                                        }
                                        category.copy(items = updatedItems)
                                    }
                                }
                                showConfirmationDialog = false
                                itemToToggle = null
                            }
                        ) {
                            Text(text = stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showConfirmationDialog = false
                                itemToToggle = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MenuCategorySection(
    category: LocalMenuCategory,
    onToggleCategory: (Int) -> Unit,
    onToggleItemStatus: (LocalMenuItem) -> Unit,
    onEditItem: (LocalMenuItem) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Category Header with Dropdown Arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCategory(category.name) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(category.name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (category.expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (category.expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Category Items (shown only when expanded)
            if (category.expanded) {
                Column {
                    category.items.forEach { item ->
                        MenuItemCard(
                            item = item,
                            onToggleStatus = { onToggleItemStatus(item) },
                            onEdit = { onEditItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(item: LocalMenuItem, onToggleStatus: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Food Image
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = stringResource(item.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Food Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = stringResource(item.name),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.price,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Availability Switch
            Switch(
                checked = item.isAvailable,
                onCheckedChange = { if (item.isAvailable) onToggleStatus() }
            )
        }
    }
}



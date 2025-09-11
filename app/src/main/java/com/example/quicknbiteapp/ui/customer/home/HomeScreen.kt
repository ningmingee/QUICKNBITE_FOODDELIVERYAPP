package com.example.quicknbiteapp.ui.customer.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.res.stringResource
import com.example.quicknbiteapp.viewModel.CartViewModel
import com.example.quicknbiteapp.viewModel.HomeViewModel
import com.example.quicknbiteapp.R
// Compose
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Android location
import android.location.Location
import com.google.android.gms.location.LocationServices

// Accompanist Permissions (if still using it for runtime permission)
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.location.Geocoder
import java.util.Locale




@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    cartViewModel: CartViewModel,
    homeViewModel: HomeViewModel
) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current

    // Location permission
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Launch permission request if not granted
    LaunchedEffect(Unit) {
        when (val status = locationPermissionState.status) {
            is PermissionStatus.Granted -> {
                // Permission already granted, do nothing or proceed
            }
            is PermissionStatus.Denied -> {
                // Request permission
                locationPermissionState.launchPermissionRequest()
            }
        }

    }

    // FusedLocationProviderClient
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val currentLocation = remember { mutableStateOf<Location?>(null) }

    // Get current location when permission is granted
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status is PermissionStatus.Granted) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        currentLocation.value = location
                        // No need to update restaurants
                    }
                    .addOnFailureListener {
                        // Handle failure if needed
                    }
            } catch (e: SecurityException) {
                // Permission denied, do nothing
            }
        }
    }

    val locationName = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentLocation.value) {
        currentLocation.value?.let { location ->
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    locationName.value = addresses[0].getAddressLine(0) // full address
                }
            } catch (e: Exception) {
                locationName.value = "Unknown location"
            }
        }
    }


    val searchQuery by remember { mutableStateOf(homeViewModel.searchQuery) }
    val filteredRestaurants by remember { derivedStateOf { homeViewModel.filteredRestaurants } }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { onNavigate("cart") }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = stringResource(R.string.cart_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFC1E8FB),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate("orderTracking") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.order_tracking),
                    contentDescription = "Order Tracking",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            // Address
            item {
                locationName.value?.let { name ->
                    Text(
                        text = "Your location: $name",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = homeViewModel.searchQuery,
                    onValueChange = { homeViewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_food_restaurants),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (homeViewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { homeViewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search"
                                )
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Greeting
            item {
                Column {
                    Text(
                        text = stringResource(R.string.good_morning),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = stringResource(R.string.rise_and_shine),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Categories
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        CategoryItem("Offers", R.drawable.offer) { onNavigate("offers") }
                    }
                    item {
                        CategoryItem("Drinks", R.drawable.drink) { onNavigate("drinks") }
                    }
                    item {
                        CategoryItem("Food", R.drawable.food) { onNavigate("food") }
                    }
                    item {
                        CategoryItem("Dessert", R.drawable.desserts) { onNavigate("dessert") }
                    }
                    item {
                        CategoryItem("MealPlans", R.drawable.mealplans) { onNavigate("mealplans") }
                    }
                }
            }

            // Restaurants title
            item {
                Text(
                    stringResource(R.string.restaurant),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Restaurants list
            if (filteredRestaurants.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_restaurant),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                items(filteredRestaurants) { restaurant ->
                    RestaurantItem(
                        title = restaurant.name,
                        subtitle = restaurant.address,
                        imageRes = restaurant.imageRes,
                        onClick = if (restaurant.hasMenu) {
                            { onNavigate(restaurant.id) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, logo: Int, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = logo),
                contentDescription = name,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun RestaurantItem(title: String,
                   subtitle: String,
                   imageRes: Int,
                   onClick: (() -> Unit)? = null ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

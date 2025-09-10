package com.example.user_fooddelivery.ui.orderTracking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.user_fooddelivery.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Tracking") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier   // ✅ use modifier from NavGraph
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFEFEFEF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🗺️ Map Here", color = Color.Gray)
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fat Burger - Jalan Burma", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("77, Lembah Lorong Permai 3", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Out for delivery", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Arriving at 11:45", fontSize = 14.sp, color = Color.Gray)
                        Text("1869", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Driver",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.LightGray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Faizun Ilham", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text("5.0", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                            Text("Honda Scoopy · B 9900 RFS", fontSize = 12.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { /* Call rider */ }) {
                            Icon(Icons.Default.Call, contentDescription = "Call Rider", tint = Color.Green)
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tip your shopper", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Everyone deserve a little kindness", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TipButton("RM 1.00")
                        TipButton("RM 2.00")
                        TipButton("RM 5.00")
                    }
                }
            }
        }
    }
}

@Composable
fun TipButton(text: String) {
    Button(
        onClick = { /* Tip action */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier
            .width(100.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, color = Color.Black)
    }
}

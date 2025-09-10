package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quicknbiteapp.data.model.Review
import com.example.quicknbiteapp.data.model.ReviewStats
import com.example.quicknbiteapp.viewmodel.VendorViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorReviewsScreen(
    viewModel: VendorViewModel
) {
    val reviews by viewModel.reviews.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadVendorReviews() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Customer Reviews") }) }
    ) { padding ->
        if (reviews.isEmpty()) {
            EmptyReviewsState()
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(reviews) { review ->
                    CustomerReviewItem(review = review)
                }
            }
        }
    }
}

@Composable
fun EmptyReviewsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Star,
            contentDescription = "No reviews",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No Customer Reviews Yet",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            "Customer reviews will appear here once they rate your service",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun CustomerReviewItem(review: Review) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    review.userName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                StarRating(rating = review.rating)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment)
            review.createdAt?.toDate()?.let { date ->
                Text(
                    formatDate(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ReviewsStatsCard(reviewStats: ReviewStats, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if (reviewStats.totalReviews > 0) {
                    navController.navigate("vendor/reviews")
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Customer Reviews",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (reviewStats.totalReviews > 0) {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    Text(
                        "${reviewStats.averageRating}/5",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "(${reviewStats.totalReviews} reviews)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                StarRating(rating = reviewStats.averageRating)
            } else {
                // Show "No reviews" message
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No reviews yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Average Rating",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Add a button to view all reviews
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate("vendor/reviews") },
                modifier = Modifier.fillMaxWidth(),
                enabled = reviewStats.totalReviews > 0
            ) {
                Text("View All Reviews")
            }
        }
    }
}

@Composable
fun StarRating(rating: Float, maxStars: Int = 5) {
    Row {
        for (i in 1..maxStars) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) Color(0xFFFFD700) else Color(0xFFCCCCCC),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "%.1f".format(rating),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun Any?.toDate(): Date? {
    return when (this) {
        is com.google.firebase.Timestamp -> this.toDate()
        is java.util.Date -> this
        is Long -> Date(this)
        else -> null
    }
}

// Add this date formatting function
fun formatDate(date: Date): String {
    return SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(date)
}
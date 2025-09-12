package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.data.model.Review
import com.example.quicknbiteapp.data.model.ReviewStats
import com.example.quicknbiteapp.viewModel.VendorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorReviewsScreen(
    viewModel: VendorViewModel,
    navController: NavController? = null
) {
    val reviews by viewModel.reviews.collectAsState()
    val reviewStats by viewModel.reviewStats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVendorReviews()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Reviews") },
                navigationIcon = {
                    // ADD BACK BUTTON with null check
                    if (navController != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadVendorReviews() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }

            )
        }
    ) { padding ->
        if (reviews.isEmpty()) {
            EmptyReviewsState()
        } else {
            Column(modifier = Modifier.padding(padding)) {
                // Show review statistics at the top
                ReviewStatsHeader(reviewStats = reviewStats)

                LazyColumn {
                    items(reviews) { review ->
                        CustomerReviewItem(review = review)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewStatsHeader(reviewStats: ReviewStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.average_rating),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "%.1f/5".format(reviewStats.averageRating),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            StarRating(rating = reviewStats.averageRating)
            Text(
                text = "Based on ${reviewStats.totalReviews} reviews",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            text = "No Customer Reviews Yet",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Customer reviews will appear here once they rate your service",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun CustomerReviewItem(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with user info and rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = review.userName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                StarRating(rating = review.rating)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(review.comment)
            Text(
                text = review.getFormattedDate(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Review comment
            if (review.comment.isNotBlank()) {
                Text(
                    review.comment,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    "No comment provided",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }

            // Order reference if available
            if (review.orderId.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Order #${review.orderId.take(15)}",
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
                onClick = {
                    navController.navigate("vendor/reviews")
                },
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
package com.example.quicknbiteapp.data.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Review(
    val reviewId: String = "",
    val vendorId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val orderId: String = "",
    val createdAt: Any? = null,
    val updatedAt: Any? = null
) {
    // Date formatted
    fun getFormattedDate(): String {
        return when (createdAt) {
            is Timestamp -> {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                dateFormat.format(createdAt.toDate())
            }

            is Long -> {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                dateFormat.format(Date(createdAt))
            }

            is Date -> {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                dateFormat.format(createdAt)
            }

            else -> "Date not available"
        }
    }
}


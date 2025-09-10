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
){
    fun getCreatedDate(): Date? {
        return when (createdAt) {
            is Timestamp -> (createdAt as Timestamp).toDate()
            is Long -> Date(createdAt as Long)
            is String -> try { SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).parse(createdAt as String) } catch (e: Exception) { null }
            else -> null
        }
    }
}

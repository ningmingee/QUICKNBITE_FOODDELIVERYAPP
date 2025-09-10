package com.example.quicknbiteapp.repository

import com.example.quicknbiteapp.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VendorSettingsRepository (
    private val firestore: FirebaseFirestore
) {
    suspend fun getVendorSettings(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateVendorSettings(userId: String, updates: Map<String, Any>): Boolean {
        return try {
            firestore.collection("users").document(userId).update(updates).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateBusinessInfo(
        userId: String,
        businessName: String,
        address: String,
        operatingHours: String
    ): Boolean {
        val updates = mapOf(
            "businessName" to businessName,
            "businessAddress" to address,
            "operatingHours" to operatingHours
        )
        return updateVendorSettings(userId, updates)
    }

    suspend fun updateAccountInfo(userId: String, displayName: String, phoneNumber: String): Boolean {
        val updates = mapOf(
            "displayName" to displayName,
            "phoneNumber" to phoneNumber
        )
        return updateVendorSettings(userId, updates)
    }

    suspend fun updateNotificationSettings(userId: String, pushEnabled: Boolean, emailEnabled: Boolean): Boolean {
        return try {
            val updates = mapOf(
                "pushNotifications" to pushEnabled,
                "emailNotifications" to emailEnabled,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("users").document(userId).update(updates).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

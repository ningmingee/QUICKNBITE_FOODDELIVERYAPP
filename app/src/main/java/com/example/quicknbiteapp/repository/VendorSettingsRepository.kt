package com.example.quicknbiteapp.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.quicknbiteapp.data.model.User
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
            Log.e(TAG, "Error getting vendor settings: ${e.message}")
            null
        }
    }

    suspend fun updateBusinessInfo(
        userId: String,
        businessName: String,
        address: String,
        operatingHours: String
    ): Boolean {
        return try {
            val updates = mapOf(
                "businessName" to businessName,
                "businessAddress" to address,
                "operatingHours" to operatingHours
            )
            firestore.collection("users").document(userId).update(updates).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating business info: ${e.message}")
            false
        }
    }

    suspend fun updateAccountInfo(userId: String, displayName: String, phoneNumber: String): Boolean {
        return try {
            val updates = mapOf(
                "displayName" to displayName,
                "phoneNumber" to phoneNumber
            )

            firestore.collection("users").document(userId).update(updates).await()
            true
        } catch (_: Exception) {
            false
        }
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
        } catch (_: Exception) {
            false
        }
    }
}

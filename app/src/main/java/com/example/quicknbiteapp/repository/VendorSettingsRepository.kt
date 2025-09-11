package com.example.quicknbiteapp.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.quicknbiteapp.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    suspend fun setProfileImage(vendorId: String, imageUri: Uri): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("vendor_profiles/$vendorId/${System.currentTimeMillis()}.jpg")

            val uploadTask = imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image: ${e.message}")
            null
        }
    }

    suspend fun updateProfileImage(vendorId: String, imageUrl: String): Boolean {
        return try {
            FirebaseFirestore.getInstance().collection("vendors")
                .document(vendorId)
                .update("profileImageUrl", imageUrl)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile image: ${e.message}")
            false
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
        return try {
            val updates = mapOf(
                "businessName" to displayName,
                "phoneNumber" to phoneNumber
            )

            FirebaseFirestore.getInstance().collection("vendors")
                .document(userId)
                .update(updates)
                .await()

            true
        } catch (e: Exception) {
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
        } catch (e: Exception) {
            false
        }
    }
}

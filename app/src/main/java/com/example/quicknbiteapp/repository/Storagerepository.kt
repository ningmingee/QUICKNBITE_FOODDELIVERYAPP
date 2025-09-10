package com.example.quicknbiteapp.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {
    private val storage = FirebaseStorage.getInstance()
    private val profileImagesRef = storage.reference.child("profile_images")

    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        val imageRef = profileImagesRef.child("$userId/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    suspend fun deleteProfileImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
        }
    }
}


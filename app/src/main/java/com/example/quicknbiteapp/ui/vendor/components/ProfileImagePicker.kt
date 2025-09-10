package com.example.quicknbiteapp.ui.vendor.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.quicknbiteapp.utils.rememberImagePickerLauncher

@Composable
fun ProfileImagePicker(
    imageUrl: String,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    showEditIcon: Boolean = true,
    isLoading: Boolean = false
) {
    var showImagePickerOptions by remember { mutableStateOf(false) }

    // Use the new launcher
    val (pickFromGallery, takeFromCamera) = rememberImagePickerLauncher { uri ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { showImagePickerOptions = true },
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Add Profile Photo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { showImagePickerOptions = true },
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (showEditIcon && !isLoading) {
            IconButton(
                onClick = { showImagePickerOptions = true },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showImagePickerOptions) {
            ImagePickerDialog(
                onDismiss = { showImagePickerOptions = false },
                onGallerySelect = { pickFromGallery() }, // Call the gallery launcher
                onCameraSelect = { takeFromCamera() }    // Call the camera launcher
            )
        }
    }
}

@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onGallerySelect: () -> Unit,
    onCameraSelect: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Profile Photo") },
        text = {
            Column { // Use a Column to list options
                Text("Select an option to choose your profile photo:")
                Spacer(Modifier.height(16.dp))
                Button( // Gallery Button
                    onClick = {
                        onGallerySelect()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoLibrary, "Gallery", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Choose from Gallery")
                }
                Spacer(Modifier.height(8.dp))
                Button( // Camera Button
                    onClick = {
                        onCameraSelect()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, "Camera", modifier = Modifier.size(20.dp)) // Added CameraAlt Icon
                    Spacer(Modifier.width(8.dp))
                    Text("Take Photo")
                }
            }
        },
        confirmButton = { // Or use dismissButton for "Cancel", or have no buttons if actions are in text
            Button(
                onClick = onDismiss,
            ) {
                Text("Cancel")
            }
        }
    )
}

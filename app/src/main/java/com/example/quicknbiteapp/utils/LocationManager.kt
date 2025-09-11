package com.example.quicknbiteapp.utils

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    var userLocation = mutableStateOf<UserLocation?>(null)
        private set
    fun fetchUserLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = if (!addresses.isNullOrEmpty()) addresses[0].getAddressLine(0) else "Unknown location"
                    userLocation.value = UserLocation(location.latitude, location.longitude, address)
                }
            }
        } catch (e: SecurityException) {
                // Permission denied
            userLocation.value = null
        }
    }
}

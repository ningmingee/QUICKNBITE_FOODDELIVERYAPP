package com.example.quicknbiteapp.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknbiteapp.data.model.Order
import com.example.quicknbiteapp.data.model.OrderStatus
import com.example.quicknbiteapp.data.model.Review
import com.example.quicknbiteapp.data.model.ReviewStats
import com.example.quicknbiteapp.data.model.User
import com.example.quicknbiteapp.repository.FirestoreVendorRepository
import com.example.quicknbiteapp.repository.StorageRepository
import com.example.quicknbiteapp.repository.VendorRepository
import com.example.quicknbiteapp.repository.VendorSettingsRepository
import com.example.quicknbiteapp.ui.state.VendorUiState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VendorViewModel : ViewModel() {
    private val TAG = "VendorViewModel"

    private val vendorRepository: VendorRepository by lazy {
        FirestoreVendorRepository(FirebaseFirestore.getInstance())
    }

    private val _uiState = MutableStateFlow<VendorUiState>(VendorUiState.Loading)
    val uiState: StateFlow<VendorUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _reviewStats = MutableStateFlow(ReviewStats())
    val reviewStats: StateFlow<ReviewStats> = _reviewStats.asStateFlow()
    private val settingsRepository = VendorSettingsRepository(FirebaseFirestore.getInstance())

    private val _vendorSettings = MutableStateFlow<User?>(null)
    val vendorSettings: StateFlow<User?> = _vendorSettings.asStateFlow()
    private val storageRepository = StorageRepository()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        Log.d(TAG, "ViewModel created")
        loadVendorData()
        debugFirestorePaths()
    }

    fun getCurrentVendorId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val vendorId = currentUser?.uid ?: "test_vendor_001" // Fallback for testing

        Log.d(TAG, "=== CURRENT USER INFO ===")
        Log.d(TAG, "Firebase Auth Current User: $currentUser")
        Log.d(TAG, "User UID: ${currentUser?.uid}")
        Log.d(TAG, "User Email: ${currentUser?.email}")
        Log.d(TAG, "User Display Name: ${currentUser?.displayName}")
        Log.d(TAG, "Is User Authenticated: ${currentUser != null}")
        Log.d(TAG, "Using Vendor ID: $vendorId")
        Log.d(TAG, "=== END USER INFO ===")

        return vendorId
    }

    fun debugFirestorePaths() {
        viewModelScope.launch {
            val vendorId = getCurrentVendorId()
            Log.d(TAG, "=== FIRESTORE PATH DEBUG ===")
            Log.d(TAG, "Checking existence of documents for vendorId: $vendorId")

            // Check vendors collection
            try {
                val vendorDoc = FirebaseFirestore.getInstance().collection("vendors").document(vendorId).get().await()
                Log.d(TAG, "Vendors collection - Document exists: ${vendorDoc.exists()}")
                if (vendorDoc.exists()) {
                    Log.d(TAG, "Vendors document data: ${vendorDoc.data}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking vendors collection: ${e.message}")
            }

            // Check users collection
            try {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(vendorId).get().await()
                Log.d(TAG, "Users collection - Document exists: ${userDoc.exists()}")
                if (userDoc.exists()) {
                    Log.d(TAG, "Users document data: ${userDoc.data}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking users collection: ${e.message}")
            }

            // Check menuItems collection
            try {
                val menuItemsSnapshot = FirebaseFirestore.getInstance().collection("menuItems")
                    .whereEqualTo("vendorId", vendorId)
                    .get()
                    .await()
                Log.d(TAG, "MenuItems collection - Found ${menuItemsSnapshot.documents.size} documents")
                menuItemsSnapshot.documents.forEachIndexed { index, doc ->
                    Log.d(TAG, "MenuItem $index: ${doc.id} - ${doc.data}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking menuItems collection: ${e.message}")
            }

            // Check orders collection
            try {
                val ordersSnapshot = FirebaseFirestore.getInstance().collection("orders")
                    .whereEqualTo("vendorId", vendorId)
                    .get()
                    .await()
                Log.d(TAG, "Orders collection - Found ${ordersSnapshot.documents.size} documents")
                ordersSnapshot.documents.forEachIndexed { index, doc ->
                    Log.d(TAG, "Order $index: ${doc.id} - ${doc.data}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking orders collection: ${e.message}")
            }

            Log.d(TAG, "=== END FIRESTORE PATH DEBUG ===")
        }
    }

    fun loadVendorData() {
        viewModelScope.launch {
            _uiState.value = VendorUiState.Loading
            val userId = getCurrentVendorId()

            try {
                Log.d(TAG, "Loading vendor data for user: $userId")
                val vendorResult = vendorRepository.getVendorData(userId)

                if (vendorResult.isSuccess) {
                    val vendor = vendorResult.getOrThrow()
                    loadVendorReviews()

                    // DEBUG: LOG ALL THE DATA YOU'RE GETTING
                    Log.d(TAG, "=== VENDOR DATA RECEIVED ===")
                    Log.d(TAG, "Business: ${vendor.businessName}")
                    Log.d(TAG, "Monthly Revenue: ${vendor.monthlyRevenue}")
                    Log.d(TAG, "Weekly Revenue: ${vendor.weeklyRevenue}")
                    Log.d(TAG, "Total Revenue: ${vendor.totalRevenue}")
                    Log.d(TAG, "Total Orders: ${vendor.totalOrders}")
                    Log.d(TAG, "Total Customers: ${vendor.totalCustomers}")
                    Log.d(TAG, "Rating: ${vendor.rating}")
                    Log.d(TAG, "Review Count: ${vendor.reviewCount}")
                    Log.d(TAG, "Top Item: ${vendor.topSellingItem}")
                    Log.d(TAG, "Top Count: ${vendor.topSellingCount}")

                    // Load other data
                    val menuItemsResult = vendorRepository.getMenuItems(userId)
                    val ordersResult = vendorRepository.getOrders(userId)

                    if (menuItemsResult.isSuccess && ordersResult.isSuccess) {
                        _uiState.value = VendorUiState.Success(
                            vendor = vendor,
                            menuItems = menuItemsResult.getOrThrow(),
                            orders = ordersResult.getOrThrow()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading vendor data: ${e.message}")
            }
        }
    }

    fun formatOrderDate(dateValue: Any?): String {
        return try {
            when (dateValue) {
                is Timestamp -> {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    dateFormat.format(dateValue.toDate())
                }
                is Date -> {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    dateFormat.format(dateValue)
                }
                is Long -> {
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    dateFormat.format(Date(dateValue))
                }
                is String -> dateValue
                null -> "Date not set"
                else -> "Invalid date"
            }
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    fun loadVendorReviews() {
        viewModelScope.launch {
            try {
                val vendorId = getCurrentVendorId()
                Log.d(TAG, "Loading reviews for vendor: $vendorId")

                val reviewsResult = vendorRepository.getVendorReviews(vendorId)
                if (reviewsResult.isSuccess) {
                    val reviews = reviewsResult.getOrThrow()
                    Log.d(TAG, "Successfully loaded ${reviews.size} reviews")
                    _reviews.value = reviews
                    calculateReviewStats(reviews)
                } else {
                    Log.e(TAG, "Failed to load reviews: ${reviewsResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading reviews: ${e.message}")
            }
        }
    }

    private fun calculateReviewStats(reviews: List<Review>) {
        if (reviews.isEmpty()) {
            _reviewStats.value = ReviewStats()
            Log.d(TAG, "No reviews found")
            return
        }

        val averageRating = reviews.map { it.rating }.average().toFloat()
        val totalReviews = reviews.size

        _reviewStats.value = ReviewStats(
            averageRating = averageRating,
            totalReviews = totalReviews
        )
        Log.d(TAG, "Calculated stats: Average=$averageRating, Total=$totalReviews")
    }

    fun getOrderTimeline(order: Order): List<Pair<String, String>> {
        val timeline = mutableListOf<Pair<String, String>>()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Add status changes from history in chronological order
        val statusEvents = order.getStatusEvents()

        if (statusEvents.isEmpty()) {
            // If no status history, just show creation time
            timeline.add(
                dateFormat.format(order.getCreationDate()) to "Order received"
            )
        } else {
            // Add each status event with proper description
            statusEvents.forEach { (status, timestamp) ->
                val time = dateFormat.format(timestamp)
                val event = when (status) {
                    OrderStatus.PENDING -> "Order received"
                    OrderStatus.PREPARING -> "Order accepted"
                    OrderStatus.READY_FOR_PICKUP -> "Ready for pickup"
                    OrderStatus.COMPLETED -> "Order completed"
                    OrderStatus.CANCELLED -> "Order cancelled"
                }
                timeline.add(time to event)
            }
        }

        return timeline.sortedBy { it.first }
    }

    fun getDetailedOrderTimeline(order: Order): List<Pair<String, String>> {
        val timeline = mutableListOf<Pair<String, String>>()
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Always show order creation as the first event
        timeline.add(
            dateFormat.format(order.getCreationDate()) to "Order placed"
        )

        // Get status events excluding PENDING (since we already have "Order placed")
        val statusEvents = order.getStatusEvents()
            .filter { it.first != OrderStatus.PENDING }

        // Add other status events
        statusEvents.forEach { (status, timestamp) ->
            val time = dateFormat.format(timestamp)
            val event = when (status) {
                OrderStatus.PREPARING -> "Order accepted by vendor"
                OrderStatus.READY_FOR_PICKUP -> "Order ready for pickup"
                OrderStatus.COMPLETED -> "Order delivered"
                OrderStatus.CANCELLED -> "Order cancelled"
                else -> "Status updated" // Should not happen due to filter
            }
            timeline.add(time to event)
        }

        return timeline
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            try {
                vendorRepository.updateOrderStatus(orderId, status)
                // Reload data after update
                loadVendorData()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating order: ${e.message}")
            }
        }
    }

    fun loadVendorSettings() {
        viewModelScope.launch {
            try {
                val vendorId = getCurrentVendorId()
                val settings = settingsRepository.getVendorSettings(vendorId)
                _vendorSettings.value = settings
            } catch (e: Exception) {
                Log.e(TAG, "Error loading vendor settings: ${e.message}")
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val vendorId = getCurrentVendorId()
                val imageUrl = storageRepository.uploadProfileImage(vendorId, imageUri)

                // Update user document with new profile image URL
                val updates = mapOf(
                    "profileImageUrl" to imageUrl,
                    "updatedAt" to Timestamp.now()
                )

                val success = settingsRepository.updateVendorSettings(vendorId, updates)
                if (success) {
                    // Update local state
                    _vendorSettings.value = _vendorSettings.value?.copy(profileImageUrl = imageUrl)
                    loadVendorSettings() // Reload to ensure consistency
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading profile image: ${e.message}")
            }
        }
    }

    fun removeProfileImage() {
        viewModelScope.launch {
            try {
                val vendorId = getCurrentVendorId()
                val currentImageUrl = _vendorSettings.value?.profileImageUrl ?: ""

                if (currentImageUrl.isNotEmpty()) {
                    storageRepository.deleteProfileImage(currentImageUrl)
                }

                // Remove profile image URL from user document
                val updates = mapOf(
                    "profileImageUrl" to "",
                    "updatedAt" to Timestamp.now()
                )

                val success = settingsRepository.updateVendorSettings(vendorId, updates)
                if (success) {
                    _vendorSettings.value = _vendorSettings.value?.copy(profileImageUrl = "")
                    loadVendorSettings()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing profile image: ${e.message}")
            }
        }
    }

    fun updateBusinessInfo(businessName: String, address: String, operatingHours: String) {
        viewModelScope.launch {
            try {
                val vendorId = getCurrentVendorId()
                val success = settingsRepository.updateBusinessInfo(
                    vendorId, businessName, address, operatingHours
                )
                if (success) {
                    loadVendorSettings() // Reload settings
                    // Show success message
                } else {
                    // Show error message
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating business info: ${e.message}")
            }
        }
    }

    fun updateAccountInfo(displayName: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                val vendorId = getCurrentVendorId()
                val success = settingsRepository.updateAccountInfo(vendorId, displayName, phoneNumber)
                if (success) {
                    loadVendorSettings() // Reload settings
                    // Show success message
                } else {
                    // Show error message
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating account info: ${e.message}")
            }
        }
    }

    fun updateNotificationSettings(pushEnabled: Boolean, emailEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val vendorId = getCurrentVendorId()
                val success = settingsRepository.updateNotificationSettings(vendorId, pushEnabled, emailEnabled)
                if (success) {
                    _vendorSettings.value = _vendorSettings.value?.copy(
                        pushNotifications = pushEnabled,
                        emailNotifications = emailEnabled
                    )
                    loadVendorSettings()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating notification settings: ${e.message}")
            }
        }
    }

    fun refreshData() {
        Log.d(TAG, "Manual refresh triggered")
        loadVendorData()
    }
}
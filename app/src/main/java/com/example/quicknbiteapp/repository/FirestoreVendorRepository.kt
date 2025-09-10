package com.example.quicknbiteapp.repository

import android.util.Log
import com.example.quicknbiteapp.data.model.MenuItem
import com.example.quicknbiteapp.data.model.Order
import com.example.quicknbiteapp.data.model.OrderStatus
import com.example.quicknbiteapp.data.model.Review
import com.example.quicknbiteapp.data.model.Vendor
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirestoreVendorRepository (
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : VendorRepository {

    companion object {
        private const val TAG = "FirestoreVendorRepo"
    }

    override suspend fun getVendorData(userId: String): Result<Vendor> {
        return try {
            Log.d(TAG, "=== START getVendorData ===")
            Log.d(TAG, "Looking for vendor data for user ID: $userId")

            // 1. First get user document to find the vendorId
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                Log.d(TAG, "User document not found at users/$userId")
                return Result.failure(Exception("User not found"))
            }

            val userData = userDoc.data ?: emptyMap()
            val vendorIdFromUser = userData["vendorId"] as? String ?: "test_vendor_001" // DEFAULT TO test_vendor_001
            Log.d(TAG, "Using vendor ID: $vendorIdFromUser")

            // 2. Get vendor document
            val vendorDoc = firestore.collection("vendors").document(vendorIdFromUser).get().await()
            Log.d(TAG, "Vendor document exists: ${vendorDoc.exists()}")

            if (vendorDoc.exists()) {
                val vendorData = vendorDoc.data ?: emptyMap()

                // DEBUG: Log all vendor data
                Log.d(TAG, "=== VENDOR DATA FROM FIRESTORE ===")
                vendorData.forEach { (key, value) ->
                    Log.d(TAG, "$key: $value (${value?.javaClass?.simpleName})")
                }

                // 3. Create Vendor object - HANDLE ALL DATA TYPES PROPERLY
                val vendor = Vendor(
                    id = vendorDoc.id,
                    userId = userId,
                    vendorId = vendorDoc.id,
                    businessName = vendorData["businessName"] as? String ?: userData["businessName"] as? String ?: "",
                    ownerName = vendorData["ownerName"] as? String ?: "",
                    email = vendorData["email"] as? String ?: userData["email"] as? String ?: "",
                    phone = vendorData["phone"] as? String ?: userData["phoneNumber"] as? String ?: "",
                    phoneNumber = vendorData["phone"] as? String ?: userData["phoneNumber"] as? String ?: "",
                    address = vendorData["address"] as? String ?: "",
                    userType = userData["userType"] as? String ?: "vendor",

                    // HANDLE NUMERIC FIELDS - THEY MIGHT BE STRINGS OR NUMBERS
                    monthlyRevenue = parseDouble(vendorData["monthlyRevenue"]),
                    weeklyRevenue = parseDouble(vendorData["weeklyRevenue"]),
                    operatingHours = vendorData["operatingHours"] as? String ?: "",
                    rating = parseFloat(vendorData["rating"]),
                    reviewCount = parseInt(vendorData["reviewCount"]),
                    topSellingItem = vendorData["topSellingItem"] as? String ?: "",
                    topSellingCount = parseInt(vendorData["topSellingCount"]),
                    totalCustomers = parseInt(vendorData["totalCustomers"]),
                    totalOrders = parseInt(vendorData["totalOrders"]),
                    totalRevenue = parseDouble(vendorData["totalRevenue"]),

                    createdAt = vendorData["createdAt"] as Timestamp,
                    updatedAt = vendorData["updatedAt"]
                )

                Log.d(TAG, "Successfully created vendor with ALL stats")
                Result.success(vendor)
            } else {
                Log.d(TAG, "Vendor document not found at vendors/$vendorIdFromUser")
                Result.failure(Exception("Vendor not found"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting vendor data: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getVendorReviews(vendorId: String): Result<List<Review>> {
        return try {
            Log.d(TAG, "Getting reviews for vendor: $vendorId")

            val snapshot = firestore.collection("reviews")
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Found ${snapshot.documents.size} reviews")

            val reviews = snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: emptyMap()
                Log.d(TAG, "Processing document ${doc.id}: ${data.keys}")
                Review(
                    reviewId = doc.id,
                    vendorId = data["vendorId"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    userName = data["userName"] as? String ?: "Anonymous Customer",
                    rating = parseFloat(data["rating"]),
                    comment = data["comment"] as? String ?: "",
                    orderId = data["orderId"] as? String ?: "",
                    createdAt = data["createdAt"],
                    updatedAt = data["updatedAt"]
                )
            }
            Log.d(TAG, "Successfully see ${reviews.size} reviews")
            Result.success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting reviews: ${e.message}")
            Result.failure(e)
        }
    }


    private fun parseDouble(value: Any?): Double {
        return when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun parseInt(value: Any?): Int {
        return when (value) {
            is Int -> value
            is Long -> value.toInt()
            is Double -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
    }

    private fun parseFloat(value: Any?): Float {
        return when (value) {
            is Float -> value
            is Double -> value.toFloat()
            is Long -> value.toFloat()
            is Int -> value.toFloat()
            is String -> value.toFloatOrNull() ?: 0f
            else -> 0f
        }
    }



    override suspend fun getOrders(vendorId: String): Result<List<Order>> {
        return try {
            Log.d(TAG, "=== START getOrders ===")
            Log.d(TAG, "Attempting to get orders for vendorId: $vendorId")
            Log.d(TAG, "Current authenticated user UID: ${FirebaseAuth.getInstance().currentUser?.uid}")

            val snapshot = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()

            Log.d(TAG, "Orders query: Snapshot is empty? ${snapshot.isEmpty}")
            Log.d(TAG, "Orders query: Found ${snapshot.documents.size} documents.")

            // Log each document found
            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "Order document $index - ID: ${doc.id}, Data: ${doc.data}")
            }

            val orders = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Mapping order document: ${doc.id} -> ${doc.data}")
                val order = doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                if (order != null) {
                    Log.d(TAG, "Successfully mapped order: ${order.orderId}: ${order.status} - RM${order.totalAmount}")
                } else {
                    Log.d(TAG, "Failed to map order from document: ${doc.id}")
                }
                order
            }

            Log.d(TAG, "Successfully mapped ${orders.size} orders")
            Log.d(TAG, "=== END getOrders - SUCCESS ===")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting orders: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
            Log.d(TAG, "=== END getOrders - ERROR ===")
            Result.failure(e)
        }
    }

    override suspend fun getMenuItems(vendorId: String): Result<List<MenuItem>> {
        return try {
            Log.d(TAG, "=== START getMenuItems ===")
            Log.d(TAG, "Attempting to get menu items for vendorId: $vendorId")
            Log.d(TAG, "Current authenticated user UID: ${FirebaseAuth.getInstance().currentUser?.uid}")

            val snapshot = firestore.collection("menuItems")
                .whereEqualTo("vendorId", vendorId)
                .get()
                .await()

            Log.d(TAG, "MenuItems query: Snapshot is empty? ${snapshot.isEmpty}")
            Log.d(TAG, "MenuItems query: Found ${snapshot.documents.size} documents.")

            // Log each document found
            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "MenuItem document $index - ID: ${doc.id}, Data: ${doc.data}")
            }

            val menuItems = snapshot.documents.mapNotNull { doc ->
                Log.d(TAG, "Mapping menu item document: ${doc.id} -> ${doc.data}")
                val menuItem = doc.toObject(MenuItem::class.java)?.copy(itemId = doc.id)
                if (menuItem != null) {
                    Log.d(TAG, "Successfully mapped menu item: ${menuItem.itemId}: ${menuItem.name} - RM${menuItem.price}")
                } else {
                    Log.d(TAG, "Failed to map menu item from document: ${doc.id}")
                }
                menuItem
            }

            Log.d(TAG, "Successfully mapped ${menuItems.size} menu items")
            Log.d(TAG, "=== END getMenuItems - SUCCESS ===")
            Result.success(menuItems)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting menu items: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
            Log.d(TAG, "=== END getMenuItems - ERROR ===")
            Result.failure(e)
        }
    }

    override suspend fun updateMenuItemAvailability(itemId: String, isAvailable: Boolean): Result<Unit> {
        return try {
            Log.d(TAG, "=== START updateMenuItemAvailability ===")
            Log.d(TAG, "Updating menu item: $itemId, isAvailable: $isAvailable")

            firestore.collection("menuItems").document(itemId)
                .update("isAvailable", isAvailable)
                .await()

            Log.d(TAG, "Successfully updated menu item availability")
            Log.d(TAG, "=== END updateMenuItemAvailability - SUCCESS ===")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating menu item: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
            Log.d(TAG, "=== END updateMenuItemAvailability - ERROR ===")
            Result.failure(e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            Log.d(TAG, "=== START updateOrderStatus ===")
            Log.d(TAG, "Updating order: $orderId, status: $status")

            val updateData = mapOf(
                "status" to status.name,
                "updatedAt" to Timestamp.now()
            )

            firestore.collection("orders").document(orderId)
                .update(updateData)
                .await()

            Log.d(TAG, "Successfully updated order status")
            Log.d(TAG, "=== END updateOrderStatus - SUCCESS ===")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
            Log.d(TAG, "=== END updateOrderStatus - ERROR ===")
            Result.failure(e)
        }
    }

    // Add detailed logging to the helper methods as well
    private suspend fun calculateVendorStats(vendorId: String, vendor: Vendor): Vendor {
        Log.d(TAG, "=== START calculateVendorStats ===")
        Log.d(TAG, "Calculating stats for vendor: $vendorId")

        val ordersResult = getOrders(vendorId)

        if (ordersResult.isFailure) {
            Log.w(TAG, "Failed to get orders for stats calculation: ${ordersResult.exceptionOrNull()?.message}")
            Log.d(TAG, "=== END calculateVendorStats - FAILURE ===")
            return vendor
        }

        val orders = ordersResult.getOrThrow()
        Log.d(TAG, "Found ${orders.size} orders for stats calculation")

        val completedOrders = orders.filter { it.status == OrderStatus.COMPLETED }
        val pendingOrders = orders.filter { it.status == OrderStatus.PENDING }

        // Calculate time-based revenues
        val weeklyRevenue = calculateTimeBasedRevenue(completedOrders, Calendar.WEEK_OF_YEAR)
        val monthlyRevenue = calculateTimeBasedRevenue(completedOrders, Calendar.MONTH)

        // Calculate top selling item
        val topSelling = calculateTopSellingItem(completedOrders)

        Log.d(TAG, "Stats calculated - Total Orders: ${orders.size}, Completed: ${completedOrders.size}, Pending: ${pendingOrders.size}")
        Log.d(TAG, "=== END calculateVendorStats - SUCCESS ===")

        return vendor.copy(
            totalCustomers = orders.map { it.customerId }.distinct().count(),
            totalOrders = orders.size,
            totalRevenue = completedOrders.sumOf { it.totalAmount },
            weeklyRevenue = weeklyRevenue,
            monthlyRevenue = monthlyRevenue,
            pendingOrders = pendingOrders.size,
            completedOrders = completedOrders.size,
            topSellingItem = topSelling.first,
            topSellingCount = topSelling.second,
        )
    }

    private fun calculateTimeBasedRevenue(orders: List<Order>, timeUnit: Int): Double {
        Log.d(TAG, "Calculating time-based revenue for timeUnit: $timeUnit")
        if (orders.isEmpty()) {
            Log.d(TAG, "No orders found for time-based revenue calculation")
            return 0.0
        }

        val calendar = Calendar.getInstance()
        calendar.add(timeUnit, -1)
        val timeAgo = calendar.time

        val filteredOrders = orders.filter { order ->
            order.getCreationDate().after(timeAgo)
        }

        Log.d(TAG, "Found ${filteredOrders.size} orders in the specified time period")
        return filteredOrders.sumOf { it.totalAmount }
    }

    private fun calculateTopSellingItem(orders: List<Order>): Pair<String, Int> {
        Log.d(TAG, "Calculating top selling item from ${orders.size} orders")
        val itemCounts = mutableMapOf<String, Int>()

        orders.forEach { order ->
            order.items.forEach { item ->
                val itemName = item.getItemName()
                val count = itemCounts.getOrDefault(itemName, 0)
                itemCounts[itemName] = count + item.quantity
                Log.d(TAG, "Item: $itemName, Quantity: ${item.quantity}, Total so far: ${itemCounts[itemName]}")
            }
        }

        return if (itemCounts.isNotEmpty()) {
            val topItem = itemCounts.maxByOrNull { it.value }!!
            Log.d(TAG, "Top selling item: ${topItem.key} with ${topItem.value} sales")
            Pair(topItem.key, topItem.value)
        } else {
            Log.d(TAG, "No items found for top selling calculation")
            Pair("No items", 0)
        }
    }

    suspend fun getActiveOrders(vendorId: String): Result<List<Order>> {
        return try {
            Log.d(TAG, "=== START getActiveOrders ===")
            Log.d(TAG, "Getting active orders for vendorId: $vendorId")

            val snapshot = firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .whereIn("status",
                    listOf(
                        OrderStatus.PENDING.name,
                        OrderStatus.PREPARING.name,
                        OrderStatus.READY_FOR_PICKUP.name
                    )
                )
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Active orders query: Found ${snapshot.documents.size} documents.")
            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "Active order $index - ID: ${doc.id}, Status: ${doc.get("status")}")
            }

            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(orderId = doc.id)
            }

            Log.d(TAG, "Successfully mapped ${orders.size} active orders")
            Log.d(TAG, "=== END getActiveOrders - SUCCESS ===")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting active orders: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
            Log.d(TAG, "=== END getActiveOrders - ERROR ===")
            Result.failure(e)
        }
    }

    suspend fun getOrdersWithListener(vendorId: String, onUpdate: (List<Order>) -> Unit) {
        try {
            Log.d(TAG, "Setting up real-time orders listener for vendorId: $vendorId")

            firestore.collection("orders")
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error in orders listener: ${error.message}")
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        Log.d(TAG, "Real-time orders update: ${it.documents.size} documents")
                        val orders = it.documents.mapNotNull { doc ->
                            doc.toObject(Order::class.java)?.copy(orderId = doc.id)
                        }
                        Log.d(TAG, "Mapped ${orders.size} orders from real-time update")
                        onUpdate(orders)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up orders listener: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
        }
    }
}
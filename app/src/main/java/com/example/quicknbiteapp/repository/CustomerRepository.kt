package com.example.quicknbiteapp.repository

import com.example.quicknbiteapp.data.model.Customer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await


class CustomerRepository(private val firestore: FirebaseFirestore) {
    suspend fun getCustomer(customerId: String): Customer? {
        return try {
            val document = firestore.collection("customers").document(customerId).get().await()
            document.toObject<Customer>()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun updateCustomer(customerId: String, updates: Map<String, Any>): Boolean {
        return try {
            firestore.collection("customers").document(customerId).update(updates).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun createCustomer(customer: Customer): Boolean {
        return try {
            firestore.collection("customers").document(customer.customerId).set(customer.toMap()).await()
            true
        } catch (_: Exception) {
            false
        }
    }
}
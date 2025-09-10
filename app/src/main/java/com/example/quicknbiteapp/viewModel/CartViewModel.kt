package com.example.quicknbiteapp.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quicknbiteapp.data.model.CartItem
import com.example.quicknbiteapp.data.model.CartSummary
import com.example.quicknbiteapp.data.model.DeliveryOption


// --- Payment Method Enum ---
enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    ONLINE_BANKING("Online Banking"),
    CREDIT_DEBIT_CARD("Credit/Debit Card")
}

class CartViewModel : ViewModel() {

    // --- Cart Items ---
    val cartItems = mutableStateListOf<CartItem>()

    // --- Delivery ---
    var deliveryOption by mutableStateOf(DeliveryOption.STANDARD)
    var deliveryTime by mutableStateOf("")  // store user-typed delivery time

    // --- Points earned ---
    var bonusPoints by mutableIntStateOf(0)
        private set


    // --- Payment Method ---
    var paymentMethod by mutableStateOf("Cash") // default payment method
        private set

    fun selectPaymentMethod(option: String) {
        paymentMethod = option
    }

    // --- Cart item operations ---
    fun addToCart(item: CartItem) {
        val existing = cartItems.find { it.name == item.name }
        if (existing != null) existing.increaseQuantity()
        else cartItems.add(item)
    }

    fun removeItem(item: CartItem) {
        cartItems.remove(item)
    }

    fun increaseItemQuantity(item: CartItem) {
        cartItems.find { it.name == item.name }?.increaseQuantity()
    }

    fun decreaseItemQuantity(item: CartItem) {
        val target = cartItems.find { it.name == item.name }
        target?.decreaseQuantity()
        if (target != null && target.quantity == 0) cartItems.remove(target)
    }

    fun clearCart() {
        cartItems.clear()
    }

    // --- Delivery option ---
    fun selectDeliveryOption(option: DeliveryOption) {
        deliveryOption = option
    }

    // --- Delivery time ---
    fun selectDeliveryTime(time: String) {
        deliveryTime = time
    }

    // --- Summary ---
    fun getSummary(): CartSummary {
        val subtotal = cartItems.sumOf { it.getTotalPrice() }
        val deliveryFee = deliveryOption.fee
        return CartSummary(subtotal, deliveryFee)
    }

    // --- Points ---
    fun getSubtotalPoints(): Int = (cartItems.sumOf { it.getTotalPrice() } / 10).toInt()
    fun getTotalPoints(): Int = getSubtotalPoints() + bonusPoints

    fun addPoints(amount: Int) {
        bonusPoints += amount
    }

    fun resetBonusPoints() {
        bonusPoints = 0
    }

    fun getRedeemDiscount(): Double {
        // 10 points = RM0.10
        val totalPoints = getTotalPoints()
        return (totalPoints / 10) * 0.10
    }
}

package com.example.foodapp.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.math.BigDecimal

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val CART_ITEMS = stringPreferencesKey("cart_items")
    }

    private object PreferencesKeys {
        val KEY_SUBTOTAL = stringPreferencesKey("key_subtotal")
        val KEY_TAX = stringPreferencesKey("key_tax")
        val KEY_DELIVERY_FEE = stringPreferencesKey("key_delivery_fee")
        val KEY_TOTAL_AMOUNT = stringPreferencesKey("key_total_amount")
    }

    // Lấy danh sách CartItem từ DataStore dưới dạng Flow
    fun getCartItems(): Flow<List<CartItem>> {
        return dataStore.data.map { preferences ->
            val json = preferences[Keys.CART_ITEMS] ?: "[]"
            Gson().fromJson(json, object : TypeToken<List<CartItem>>() {}.type) ?: emptyList()
        }
    }

    // Lưu danh sách CartItem vào DataStore + Cập nhật lại Checkout
    suspend fun saveCartItems(cartItems: List<CartItem>) {
        try {
            val json = Json.encodeToString(cartItems)
            dataStore.edit { preferences ->
                preferences[Keys.CART_ITEMS] = json
            }
            updateCheckoutDetails(cartItems) // 🔥 Đảm bảo cập nhật checkout sau khi lưu giỏ hàng
        } catch (e: Exception) {
            Log.e("CartRepository", "Lỗi khi lưu giỏ hàng: ${e.message}")
            throw Exception("Không thể lưu giỏ hàng. Vui lòng thử lại sau.")
        }
    }


    // 🔥 Xóa sản phẩm khỏi giỏ hàng
    suspend fun clearCartItems(cartItemsToRemove: List<CartItem>) {
        try {
            val currentItems = getCartItems().firstOrNull() ?: emptyList()

            if (cartItemsToRemove.isEmpty()) return

            val updatedItems = currentItems.filterNot { item ->
                cartItemsToRemove.any { it.id == item.id }
            }

            dataStore.edit { preferences ->
                if (updatedItems.isEmpty()) {

                    preferences.remove(Keys.CART_ITEMS)
                } else {

                    val json = Json.encodeToString(updatedItems)
                    preferences[Keys.CART_ITEMS] = json
                }
            }

            updateCheckoutDetails(updatedItems)

        } catch (e: Exception) {
            Log.e("CartRepository", "Lỗi khi xóa sản phẩm trong giỏ hàng: ${e.localizedMessage}")
        }
    }

    fun getCartSize(): Flow<Int> {
        return getCartItems().map { it.size }
    }



    // Lấy chi tiết thanh toán từ DataStore
    fun getCheckoutDetails(): Flow<CheckoutDetails> {
        return dataStore.data.map { preferences ->
            CheckoutDetails(
                subTotal = preferences[PreferencesKeys.KEY_SUBTOTAL]?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                tax = preferences[PreferencesKeys.KEY_TAX]?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                deliveryFee = preferences[PreferencesKeys.KEY_DELIVERY_FEE]?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                totalAmount = preferences[PreferencesKeys.KEY_TOTAL_AMOUNT]?.toBigDecimalOrNull() ?: BigDecimal.ZERO
            )
        }
    }

    // Lưu chi tiết thanh toán vào DataStore
    private suspend fun saveCheckoutDetails(checkoutDetails: CheckoutDetails) {
        try {
            dataStore.edit {
                it[PreferencesKeys.KEY_SUBTOTAL] = checkoutDetails.subTotal.toPlainString()
                it[PreferencesKeys.KEY_TAX] = checkoutDetails.tax.toPlainString()
                it[PreferencesKeys.KEY_DELIVERY_FEE] = checkoutDetails.deliveryFee.toPlainString()
                it[PreferencesKeys.KEY_TOTAL_AMOUNT] = checkoutDetails.totalAmount.toPlainString()
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Lỗi khi lưu chi tiết thanh toán: ${e.localizedMessage}")
        }
    }

    // Cập nhật lại chi tiết thanh toán dựa trên giỏ hàng hiện tại
    private suspend fun updateCheckoutDetails(cartItems: List<CartItem>) {
        val subTotal = cartItems.fold(BigDecimal.ZERO) { acc, item ->
            acc + item.menuItemId.price.multiply(BigDecimal(item.quantity))
        }

        val tax = subTotal.multiply(BigDecimal("0.1")) // 10% thuế
        val deliveryFee = if (subTotal >= BigDecimal("500000")) BigDecimal.ZERO else BigDecimal("15000")
        val totalAmount = subTotal + tax + deliveryFee

        saveCheckoutDetails(
            CheckoutDetails(
                subTotal = subTotal,
                tax = tax,
                deliveryFee = deliveryFee,
                totalAmount = totalAmount
            )
        )
    }
}
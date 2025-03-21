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
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException
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
        val KEY_SUBTOTAL = floatPreferencesKey("key_subtotal")
        val KEY_TAX = floatPreferencesKey("key_tax")
        val KEY_DELIVERY_FEE = floatPreferencesKey("key_delivery_fee")
        val KEY_TOTAL_AMOUNT = floatPreferencesKey("key_total_amount")
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

    // 🔥 Thêm hoặc cập nhật sản phẩm trong giỏ hàng
    suspend fun addToCart(cartItem: CartItem) {
        getCartItems().firstOrNull()?.let { currentItems ->
            val updatedItems = currentItems.toMutableList()
            val existingIndex = updatedItems.indexOfFirst { it.id == cartItem.id }

            if (existingIndex != -1) {
                // Nếu đã có, cập nhật số lượng
                val existingItem = updatedItems[existingIndex]
                updatedItems[existingIndex] = existingItem.copy(quantity = cartItem.quantity)
            } else {
                // Nếu chưa có, thêm mới
                updatedItems.add(cartItem)
            }
            saveCartItems(updatedItems)

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


    // 🔥 Xóa toàn bộ giỏ hàng
    suspend fun clearCart() {
        saveCartItems(emptyList()) // Gọi hàm lưu với danh sách rỗng
    }

    // Lấy chi tiết thanh toán từ DataStore
    fun getCheckoutDetails(): Flow<CheckoutDetails> {
        return dataStore.data.map { preferences ->
            CheckoutDetails(
                subTotal = preferences[PreferencesKeys.KEY_SUBTOTAL] ?: 0f,
                tax = preferences[PreferencesKeys.KEY_TAX] ?: 0f,
                deliveryFee = preferences[PreferencesKeys.KEY_DELIVERY_FEE] ?: 0f,
                totalAmount = preferences[PreferencesKeys.KEY_TOTAL_AMOUNT] ?: 0f
            )
        }
    }

    // Lưu chi tiết thanh toán vào DataStore
    private suspend fun saveCheckoutDetails(checkoutDetails: CheckoutDetails) {
        try {
            dataStore.edit {
                it[PreferencesKeys.KEY_SUBTOTAL] = checkoutDetails.subTotal
                it[PreferencesKeys.KEY_TAX] = checkoutDetails.tax
                it[PreferencesKeys.KEY_DELIVERY_FEE] = checkoutDetails.deliveryFee
                it[PreferencesKeys.KEY_TOTAL_AMOUNT] = checkoutDetails.totalAmount
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Lỗi khi lưu chi tiết thanh toán: ${e.localizedMessage}")
        }
    }

    // Cập nhật lại chi tiết thanh toán dựa trên giỏ hàng hiện tại
    private suspend fun updateCheckoutDetails(cartItems: List<CartItem>) {
        val subTotal = cartItems.sumOf { it.quantity * it.menuItemId.price.toDouble() }.toFloat()
        val tax = subTotal * 0.1f // Giả sử thuế là 10%
        val deliveryFee = if (subTotal >= 500000f) 0f else 15000f
        val totalAmount = subTotal + tax + deliveryFee

        saveCheckoutDetails(CheckoutDetails(subTotal = subTotal, tax = tax, deliveryFee = deliveryFee, totalAmount = totalAmount))
    }
}
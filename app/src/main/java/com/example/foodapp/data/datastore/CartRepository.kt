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

    suspend fun getCartItems(): List<CartItem> {
        val json = dataStore.data.map { preferences ->
            preferences[Keys.CART_ITEMS] ?: "[]"
        }.first()
        return Gson().fromJson(json, object : TypeToken<List<CartItem>>() {}.type)
    }

    suspend fun saveCartItems(cartItems: List<CartItem>) {
        try {
            val json = Json.encodeToString(cartItems)
            dataStore.edit { preferences ->
                preferences[Keys.CART_ITEMS] = json
            }
        } catch (e: IOException) {
            Log.e("CartRepository", "Lỗi khi lưu giỏ hàng: ${e.message}")
            throw Exception("Không thể lưu giỏ hàng. Vui lòng thử lại sau.")
        } catch (e: JsonSyntaxException) {
            Log.e("CartRepository", "Dữ liệu giỏ hàng bị lỗi: ${e.message}")
            throw Exception("Dữ liệu giỏ hàng không hợp lệ. Hãy xóa giỏ hàng và thử lại.")
        } catch (e: Exception) {
            Log.e("CartRepository", "Lỗi không xác định: ${e.message}")
            throw Exception("Lỗi không xác định. Vui lòng thử lại.")
        }
    }

    suspend fun addToCart(cartItem: CartItem) {
        val currentItems = getCartItems().toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.id == cartItem.id }
        if (existingItemIndex != -1) {

            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] =
                existingItem.copy(quantity = existingItem.quantity + cartItem.quantity)
        } else {
            currentItems.add(cartItem)
        }
        saveCartItems(currentItems)
        updateCheckoutDetails(currentItems)
    }

    suspend fun removeFromCart(cartItem: CartItem) {
        val currentItems = getCartItems().toMutableList()
        currentItems.removeAll { it.id == cartItem.id }
        saveCartItems(currentItems)
        updateCheckoutDetails(currentItems)
    }

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

    suspend fun saveCheckoutDetails(checkoutDetails: CheckoutDetails) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_SUBTOTAL] = checkoutDetails.subTotal
            preferences[PreferencesKeys.KEY_TAX] = checkoutDetails.tax
            preferences[PreferencesKeys.KEY_DELIVERY_FEE] = checkoutDetails.deliveryFee
            preferences[PreferencesKeys.KEY_TOTAL_AMOUNT] = checkoutDetails.totalAmount
        }
    }

    private suspend fun updateCheckoutDetails(cartItems: List<CartItem>) {
        val subTotal = cartItems.sumOf { it.quantity * it.menuItemId.price.toDouble() }.toFloat()
        val tax = subTotal * 0.1f // Giả sử thuế là 10%
        val deliveryFee = if (subTotal > 0) 15000f else 0f
        val totalAmount = subTotal + tax + deliveryFee

        val checkoutDetails =
            CheckoutDetails(
                subTotal = subTotal,
                tax = tax,
                deliveryFee = deliveryFee,
                totalAmount = totalAmount
            )
        saveCheckoutDetails(checkoutDetails)
    }
}
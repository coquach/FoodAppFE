package com.se114.foodapp.data.dto.request

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal

data class FoodMultipartRequest(
    val name: String,
    val description: String,
    val price: BigDecimal,
    val defaultQuantity: Int
) {
    fun toPartMap(): Map<String, @JvmSuppressWildcards RequestBody> {
        val map = mutableMapOf<String, RequestBody>()

        fun add(key: String, value: String?) {
            value?.let {
                map[key] = it.toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        add("name", name)
        add("description", description)
        add("price", price.toPlainString())
        add("defaultQuantity", defaultQuantity.toString())

        return map
    }
}
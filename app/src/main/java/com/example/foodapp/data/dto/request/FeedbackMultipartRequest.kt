package com.example.foodapp.data.dto.request

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class FeedbackMultipartRequest(
    val menuItemId: Long?,
    val content: String?,
    val rating: Int
) {
    fun isRatingValid(): Boolean {
        return rating in 1..5
    }
    fun toPartMap(): Map<String, @JvmSuppressWildcards RequestBody> {
        val map = mutableMapOf<String, RequestBody>()

        fun add(key: String, value: String?) {
            value?.let {
                map[key] = it.toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }
        add("menuItemId", menuItemId.toString())
        add("content", content)
        add("rating", rating.toString())

        return map
    }
}

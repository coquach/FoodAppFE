package com.se114.foodapp.data.dto.request

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal

data class StaffMultipartRequest(
    val fullName: String,
    val position: String,
    val phone: String,
    val gender: String,
    val address: String,
    val birthDate: String, // "dd-MM-yyyy"
    val startDate: String,

    val basicSalary: BigDecimal
) {
    fun toPartMap(): Map<String, @JvmSuppressWildcards RequestBody> {
        val map = mutableMapOf<String, RequestBody>()

        fun add(key: String, value: String?) {
            value?.let {
                map[key] = it.toRequestBody("text/plain".toMediaTypeOrNull())
            }
        }

        add("fullName", fullName)
        add("position", position)
        add("phone", phone)
        add("gender", gender)
        add("address", address)
        add("birthDate", birthDate)
        add("startDate", startDate)
        add("basicSalary", basicSalary.toPlainString())

        return map
    }
}

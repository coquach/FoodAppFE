package com.example.foodapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class Address(
    val id: Long,
    val userId: String? = null,
    val name: String,
    val formatAddress: String,
    val latitude: Double?,
    val longitude: Double?,
    val country: String,
    val placeId: String,
    val defaultAddress: Boolean,
) : Parcelable
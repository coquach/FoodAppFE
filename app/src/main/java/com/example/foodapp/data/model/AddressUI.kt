package com.example.foodapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Address(
    val formatAddress: String,
    val latitude: Double,
    val longtitude: Double,
)

@Parcelize
data class AddressUI(
    val id : String = UUID.randomUUID().toString(),
    val formatAddress: String,
    val latitude: Double?,
    val longitude: Double?,
    ) : Parcelable
package com.example.foodapp.data.model

import android.os.Parcelable
import com.mapbox.maps.extension.style.expressions.dsl.generated.random
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
data class Address(
    val id : String = UUID.randomUUID().toString(),
    val formatAddress: String,
    val latitude: Double?,
    val longitude: Double?,
    ) : Parcelable
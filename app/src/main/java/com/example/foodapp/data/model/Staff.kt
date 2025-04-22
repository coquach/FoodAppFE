package com.example.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.utils.Constants.STAFF_TABLE
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = STAFF_TABLE)
data class Staff(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val fullName: String? = null,
    val position: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val imageUrl: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val birthDate: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null,

    val basicSalary: Double = 0.0,

    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,


    )
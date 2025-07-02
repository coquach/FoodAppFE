package com.example.foodapp.data.model

import android.net.Uri
import java.time.LocalDate

data class Account (
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val dob: LocalDate?= null,
    val avatar: String?=null,
)

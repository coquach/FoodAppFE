package com.example.foodapp.data.model

import android.net.Uri

data class Account (
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val provider: String = "",
    val photoUrl: Uri? = null,
)

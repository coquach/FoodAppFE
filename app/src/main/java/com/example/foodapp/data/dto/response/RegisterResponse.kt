package com.example.foodapp.data.dto.response

data class RegisterResponse(
    val uid: String,
    val tenantId: String?,
    val email: String,
    val phoneNumber: String?,
    val emailVerified: Boolean,
    val displayName: String?,
    val photoUrl: String?,
    val disabled: Boolean,
    val tokensValidAfterTimestamp: Long,
    val userMetadata: UserMetadata,
    val customClaims: Map<String, Any>,
    val providerId: String,
    val providerData: List<ProviderData>
)

data class UserMetadata(
    val creationTimestamp: Long,
    val lastSignInTimestamp: Long,
    val lastRefreshTimestamp: Long
)

data class ProviderData(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val providerId: String
)
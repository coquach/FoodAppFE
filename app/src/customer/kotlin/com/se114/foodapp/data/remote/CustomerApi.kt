package com.se114.foodapp.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface CustomerApi {

    @Multipart
    @PATCH("customers/{customerId}/avatar")
    suspend fun updateAvatar(
        @Path("customerId") customerId: String,
        @Part avatar: MultipartBody.Part
    ): Response<Unit>

}
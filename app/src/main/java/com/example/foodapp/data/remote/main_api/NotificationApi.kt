package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.FcmTokenRequest
import com.example.foodapp.data.model.Notification
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    @POST("notifications/register-token")
    suspend fun registerToken(@Body request: FcmTokenRequest): Response<String>

    @GET("notifications")
    suspend fun  getAppNotifications(
        @Query("userId") userId: String,
        @Query("isRead") isRead: Boolean?= null,
    ): Response<List<Notification>>

    @PATCH("notifications/{id}/read")
        suspend fun readNotification(@Path("id") id: Long): Response<Unit>

        @PATCH("notifications/user/{userId}/read-all")
        suspend fun readAllNotification(@Path("userId") userId: String): Response<Unit>

     @DELETE("notifications/remove-token")
     suspend fun removeToken(@Body request: FcmTokenRequest): Response<String>
}
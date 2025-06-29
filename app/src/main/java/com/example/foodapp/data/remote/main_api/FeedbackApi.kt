package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.Voucher
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedbackApi {
    @GET("foods/{foodId}/feedbacks")
    suspend fun getFeedbacksByFoodId(
        @Path("foodId") foodId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "desc",
    ): Response<PageResponse<Feedback>>
    @POST("feedbacks")
    suspend fun createFeedback(
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<Feedback>

    @PUT("feedbacks/{id}")
    suspend fun updateFeedback(
        @Path("id") id: Long,
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<Feedback>



    @DELETE("feedbacks/{id}")
    suspend fun deleteFeedback(@Path("id") id: Long): Response<Unit>

    @GET("feedbacks/order-items/{orderItemId}")
    suspend fun getFeedbacksByOrderItemId(
        @Path("orderItemId") orderItemId: Long,
    ): Response<Feedback>

}
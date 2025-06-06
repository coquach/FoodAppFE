package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

    @GET("orders/{customerId}")
    suspend fun getOrdersByCustomerId(
        @Path("customerId") customerId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Order>>

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("staffId") staffId: Long? = null,
        @Query("status") status: String? = null,
        @Query("paymentMethod") paymentMethod: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
    ): Response<PageResponse<Order>>

    @POST("orders")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Response<Order>
    
    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Long,
        @Body orderStatus: String,
    ): Response<Order>
}
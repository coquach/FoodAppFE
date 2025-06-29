package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.request.OrderStatusRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.OrderItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {



    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "desc",
        @Query("status") status: String? = null,
        @Query("notStatus") notStatus: String? = null,
        @Query("foodTableId") foodTableId: Int? = null,
        @Query("type") type: String? = null,
        @Query("customerId") customerId: String? = null,
        @Query("sellerId") sellerId: String? = null,
        @Query("shipperId") shipperId: String? = null,
        @Query("method") paymentMethod: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
    ): Response<PageResponse<Order>>

    @POST("orders")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Response<Order>
    
    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Long,
        @Body newStatus: OrderStatusRequest,
    ): Response<Unit>


    @PATCH("orders/{id}/check-out")
    suspend fun checkOutOrder(
        @Path("id") id: Long,
        @Body request: Map<String, Long?>,
    ): Response<Unit>

    @PATCH("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") id: Long,
    ): Response<Unit>

    @GET("orders/food-tables/{tableId}")
    suspend fun getOrdersByFoodTableId(
        @Path("tableId") tableId: Int,
    ): Response<Order>

  @POST("orders/{id}/order-items/batch")
  suspend fun upsertOrderItems(
      @Path("id") id: Long,
      @Body request: Map<String, List<OrderItemRequest>>
  ): Response<Order>

}
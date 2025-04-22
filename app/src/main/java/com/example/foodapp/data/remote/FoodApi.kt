package com.example.foodapp.data.remote



import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.request.RegisterRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.dto.response.RegisterResponse
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Order

import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.model.enums.OrderStatus
import com.se114.foodapp.data.dto.request.MenuRequest
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

interface FoodApi {




    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    //Order
    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("size") size: Int ,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("staffId") staffId: Long? = null,
        @Query("status") status: String? = null,
        @Query("paymentMethod") paymentMethod: String? = null,
        @Query("startDate") startDate: String? = null,      
        @Query("endDate") endDate: String? = null,
    ): PageResponse<Order>

    @POST("orders")
    fun createOrder(@Body orderRequest: OrderRequest): Response<Order>

    @PUT("orders/{id}")
    fun updateOrder(
        @Path("id") id: Long,
        @Body orderRequest: OrderRequest
    ): Response<Order>

    @PATCH("orders/{id}/status")
    fun updateOrderStatus(
        @Path("id") id: Long,
        @Body orderStatus: String
    ): Response<Order>

    // Xóa đơn hàng
    @DELETE("orders/{id}")
    fun deleteOrder(@Path("id") id: Long): Response<Void>


    //admin

    //menu

    @GET("menus/available")
    suspend fun getAvailableMenus(): Response<List<Menu>>

    @GET("menus/deleted")
    suspend fun getDeletedMenus(): Response<List<Menu>>

    @POST("menus")
    suspend fun createMenu(@Body request: MenuRequest): Response<Menu>

    @PUT("menus/{id}")
    suspend fun updateMenu(
        @Path("id") id: Long,
        @Body request: MenuRequest
    ): Response<Menu>

    @DELETE("menus/{id}")
    suspend fun deleteMenu(@Path("id") id: Long): Response<Void>

    //menu-items
    @GET("menu-items")
    suspend fun getMenuItems(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("id") id: Long? = null,
        @Query("isAvailable") isAvailable: Boolean? = null,

    ): PageResponse<MenuItem>

    @GET("menu-items/{id}")
    suspend fun getMenuItemDetailById(
        @Path("id") id: Long
    ): Response<MenuItem>

    @Multipart
    @POST("menu-items")
    suspend fun createMenuItem(
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part? = null
    ): Response<MenuItem>

    @Multipart
    @PUT("menu-items/{id}")
    suspend fun updateMenuItem(
        @Path("id") id: Long,
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part? = null
    ): Response<MenuItem>

    @DELETE("menu-items/{id}")
    suspend fun deleteMenuItem(
        @Path("id") id: Long
    ): Response<Unit>

    //staffs
    @GET("staffs")
    suspend fun getStaffs(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc"
    ): PageResponse<Staff>

    @Multipart
    @POST("staffs")
    suspend fun createStaff(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageUrl: MultipartBody.Part? = null
    ): Response<Staff>

    @Multipart
    @PUT("staffs/{id}")
    suspend fun updateStaff(
        @Path("id") id: Long,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageUrl: MultipartBody.Part? = null
    ): Response<Staff>

    @DELETE("staffs/{id}")
    suspend fun deleteStaff(@Path("id") id: Long): Response<Unit>

    @POST("staffs/caculate-salary")
    suspend fun calculateSalary(): Response<Map<String, Int>>

    @GET("staffs/total-salary")
    suspend fun getTotalSalary(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<Map<String, Double>>



}
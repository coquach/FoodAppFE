package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Menu
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
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

    @GET("menus/{menuId}/foods")
    suspend fun getFoods(
        @Path("menuId") menuId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc"
    ): Response<PageResponse<Food>>

    @Multipart
    @POST("menus/{menuId}/foods")
    suspend fun createFood(
        @Path("menuId") menuId: Long,
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part?>? = null
    ): Response<Food>

    @Multipart
    @PUT("menus/{menuId}/foods/{foodId}")
    suspend fun updateFood(
        @Path("menuId") menuId: Long,
        @Path("foodId") foodId: Long,
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part?>? = null
    ): Response<Food>


    @GET("foods/favorite")
    suspend fun getFavoriteFoods(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Food>>

    @POST("foods/{foodId}/like-toggle")
    suspend fun toggleLike(
        @Path("foodId") foodId: Long,
    ): Response<Unit>

    @PATCH("/foods/{foodId}/status-toggle")
    suspend fun toggleStatus(
        @Path("foodId") foodId: Long,
    ) : Response<Unit>
//menu
    @GET("menus")
    suspend fun getMenus(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        ) : Response<PageResponse<Menu>>
    @POST("menus")
    suspend fun createMenu(@Body request: MenuRequest): Response<Menu>

    @PUT("menus/{id}")
    suspend fun updateMenu(
        @Path("id") id: Long,
        @Body request: MenuRequest,
    ): Response<Menu>
    @PATCH("menu/{id}/status")
    suspend fun updateMenuStatus(
        @Path("id") id: Long,
        @Body status: Map<String, Boolean>
    ): Response<Unit>
}
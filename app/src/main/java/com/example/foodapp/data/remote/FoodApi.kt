package com.example.foodapp.data.remote


import com.example.foodapp.data.dto.request.ExportRequest
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.dto.request.IngredientRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Order

import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.dto.request.UnitRequest
import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.Supplier
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

interface FoodApi {


    //Order
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

    @PUT("orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: Long,
        @Body orderRequest: OrderRequest,
    ): Response<Order>

    @PATCH("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Long,
        @Body orderStatus: String,
    ): Response<Order>



    //admin

    //menu

//    @GET("menus/active")
//    suspend fun getAvailableMenus(): Response<List<Menu>>
//
//    @GET("menus/deleted")
//    suspend fun getDeletedMenus(): Response<List<Menu>>

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

    //food
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
        @Part image: MultipartBody.Part? = null
    ): Response<Food>

    @Multipart
    @PUT("menus/{menuId}/foods/{foodId}")
    suspend fun updateFood(
        @Path("menuId") menuId: Int,
        @Path("foodId") foodId: Long,
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<Food>

    @PATCH("menus/foods/{foodId}/status")
    suspend fun updateFoodStatus(
        @Path("foodId") foodId: Long,
        @Body status: Map<String, Boolean>
    ): Response<Unit>

    //staffs
    @GET("staffs")
    suspend fun getStaffs(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Staff>>

    @Multipart
    @POST("staffs")
    suspend fun createStaff(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageUrl: MultipartBody.Part? = null,
    ): Response<Staff>

    @Multipart
    @PUT("staffs/{id}")
    suspend fun updateStaff(
        @Path("id") id: Long,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageUrl: MultipartBody.Part? = null,
    ): Response<Staff>

    @DELETE("staffs/{id}")
    suspend fun deleteStaff(@Path("id") id: Long): Response<Unit>

    @POST("staffs/caculate-salary")
    suspend fun calculateSalary(): Response<Map<String, Int>>

    @GET("staffs/total-salary")
    suspend fun getTotalSalary(
        @Query("month") month: Int,
        @Query("year") year: Int,
    ): Response<Map<String, Double>>

    //Supplier
    @GET("suppliers")
    suspend fun getSuppliers(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("name") name: String? = null,
        @Query("phone") phone: String? = null,
        @Query("email") email: String? = null,
        @Query("address") address: String? = null,
        @Query("isActive") isActive: Boolean? = null,
    ): Response<PageResponse<Supplier>>

    @POST("suppliers")
    suspend fun createSupplier(
        @Body request: SupplierRequest,
    ): Response<Supplier>

    @PUT("suppliers/{id}")
    suspend fun updateSupplier(
        @Path("id") id: Long,
        @Body request: SupplierRequest,
    ): Response<Supplier>

    @PUT("suppliers/set-active/{id}")
    suspend fun setActiveSupplier(
        @Path("id") id: Long,
        @Body isActive: Boolean,
    ): Response<Unit>

    @DELETE("suppliers/{id}")
    suspend fun deleteSupplier(
        @Path("id") id: Long,
    ): Response<Unit>


    //Unit
    @GET("units/active")
    suspend fun getActiveUnits(): Response<List<com.example.foodapp.data.model.Unit>>

    @GET("units/isActive")
    suspend fun getHiddenUnits(): Response<List<com.example.foodapp.data.model.Unit>>

    @POST("units")
    suspend fun createUnit(
        @Body request: UnitRequest,
    ): Response<com.example.foodapp.data.model.Unit>

    @PUT("units/{id}")
    suspend fun updateUnit(
        @Path("id") id: Long,
        @Body request: UnitRequest,
    ): Response<com.example.foodapp.data.model.Unit>

    @DELETE("units/{id}")
    suspend fun deleteUnit(
        @Path("id") id: Long,
    ): Response<Unit>

    @PUT("units/set-active/{id}")
    suspend fun recoverUnit(
        @Path("id") id: Long,
        @Body isActive: Boolean,
    ): Response<Unit>


    //Ingredient
    @GET("ingredients/active")
    suspend fun getActiveIngredients(): Response<List<Ingredient>>

    @GET("ingredients/inActive")
    suspend fun getHiddenIngredients(): Response<List<Ingredient>>

    @GET("ingredients/{id}")
    suspend fun getIngredientById(
        @Path("id") id: Long,
    ): Response<Ingredient>

    @POST("ingredients")
    suspend fun createIngredient(
        @Body request: IngredientRequest,
    ): Response<Ingredient>

    @PUT("ingredients/{id}")
    suspend fun updateIngredient(
        @Path("id") id: Long,
        @Body request: IngredientRequest,
    ): Response<Ingredient>

    @DELETE("ingredients/{id}")
    suspend fun deleteIngredient(
        @Path("id") id: Long,
    ): Response<Unit> // 204 No Content

    @PUT("ingredients/set-active/{id}")
    suspend fun setActiveIngredient(
        @Path("id") id: Long,
        @Body isActive: Boolean,
    ): Response<Unit> // 204 No Content

    //Import


    @GET("imports")
    suspend fun getImports(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("staffId") staffId: Long? = null,
        @Query("supplierId") supplierId: Long? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
    ): Response<PageResponse<Import>>

    @POST("imports")
    suspend fun createImport(@Body request: ImportRequest): Response<Import>

    @PUT("imports/{id}")
    suspend fun updateImport(
        @Path("id") id: Long,
        @Body request: ImportRequest,
    ): Response<Import>

    @DELETE("imports/{id}")
    suspend fun deleteImport(@Path("id") id: Long): Response<Unit>


    //Export
    @GET("exports")
    suspend fun getExports(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("staffId") staffId: Long? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,

        ): Response<PageResponse<Export>>


    @POST("exports")
    suspend fun createExport(@Body request: ExportRequest): Response<Export>


    @PUT("exports/{id}")
    suspend fun updateExport(
        @Path("id") id: Long,
        @Body request: ExportRequest,
    ): Response<Export>


    @DELETE("exports/{id}")
    suspend fun deleteExport(@Path("id") id: Long): Response<Unit>

    //Inventory

    @GET("inventories")
    suspend fun getInventories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("ingredientId") ingredientId: Long? = null,
        @Query("expiryDate") expiryDate: String? = null,
        @Query("isOutOfStock") isOutOfStock: Boolean? = null,
    ): Response<PageResponse<Inventory>>

    //Voucher
    @GET("vouchers")
    suspend fun getVouchers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Voucher>>

    @POST("vouchers")
    suspend fun createVouchers(
        @Body request: VoucherRequest,
    ): Response<Voucher>

    @PUT("vouchers/{id}")
    suspend fun updateVoucher(
        @Path("id") id: Long,
        @Body request: VoucherRequest,
    ): Response<Voucher>

    @DELETE("vouchers/{id}")
    suspend fun deleteVoucher(@Path("id") id: Long): Response<Void>

    //Feedbacks
    @GET("foods/{foodId}/feedbacks")
    suspend fun getFeedbacksByFoodId(
        @Path("foodId") foodId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Feedback>>

    @Multipart
    @POST("feedbacks")
    suspend fun createFeedback(
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>? = null,
    ): Response<Feedback>

    @Multipart
    @PUT("feedbacks/{id}")
    suspend fun updateFeedback(
        @Path("id") id: Long,
        @PartMap request: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>? = null,
    ): Response<Feedback>

    @DELETE("feedbacks/{id}")
    suspend fun deleteFeedback(@Path("id") id: Long): Response<Void>


    //Customer
    @POST("customers/{customerId}/vouchers/{voucherId}")
    suspend fun receiveVoucher(
        @Path("customerId") customerId: String,
        @Path("voucherId") voucherId: Long,
    ): Response<Voucher>

    @GET("customers/{customerId}/vouchers")
    suspend fun getVouchersByCustomerId(
        @Path("customerId") customerId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Voucher>>


    @GET("orders/{customerId}")
    suspend fun getOrdersByCustomerId(
        @Path("customerId") customerId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Order>>
}
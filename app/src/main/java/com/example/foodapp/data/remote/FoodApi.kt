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




    //admin

    //menu

//    @GET("menus/active")
//    suspend fun getAvailableMenus(): Response<List<Menu>>
//
//    @GET("menus/deleted")
//    suspend fun getDeletedMenus(): Response<List<Menu>>



    //food

    //staffs


    //Supplier



   // 204 No Content

    //Import




    //Export


    //Inventory



    //Feedbacks








}
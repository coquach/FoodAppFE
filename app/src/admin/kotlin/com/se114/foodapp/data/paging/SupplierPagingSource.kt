package com.se114.foodapp.data.paging


import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.response.PageResponse

import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.paging.ApiPagingSource
import com.example.foodapp.data.remote.main_api.SupplierApi

import kotlinx.coroutines.flow.Flow


//class SupplierPagingSource(
//    private val supplierApi: SupplierApi,
//    private val filter: SupplierFilter
//) : ApiPagingSource<Supplier>() {
//    override suspend fun fetch(
//        page: Int,
//        size: Int,
//    ): Flow<ApiResponse<PageResponse<Supplier>>> {
//        return apiRequestFlow {
//            supplierApi.getSuppliers(
//                page = page,
//                size = size,
//                name = filter.name,
//                phone = filter.phone,
//                email = filter.email,
//                address = filter.address,
//                isActive = filter.isActive
//            )
//        }
//    }
//
//
//}
package com.example.foodapp.data.paging


import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.VoucherFilter

import com.example.foodapp.data.dto.response.PageResponse

import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.remote.main_api.VoucherApi
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.first

import javax.inject.Inject

class VoucherPagingSource @Inject constructor(
    private val voucherApi: VoucherApi,
    private val isCustomer: Boolean,
    private val filter: VoucherFilter,
) : ApiPagingSource<Voucher>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Voucher>>> {

        return apiRequestFlow {
            if (isCustomer) {
                voucherApi.getVouchersForCustomer(
                    page = page, size = size,
                    minQuantity = filter.minQuantity,
                    maxQuantity = filter.maxQuantity,
                    type = filter.type,
                    startDate = filter.startDate,
                    endDate = filter.endDate
                )
            } else {
                voucherApi.getVouchers(
                    page = page, size = size,
                    minQuantity = filter.minQuantity,
                    maxQuantity = filter.maxQuantity,
                    type = filter.type,
                    startDate = filter.startDate,
                    endDate = filter.endDate
                )
            }
        }
    }
}
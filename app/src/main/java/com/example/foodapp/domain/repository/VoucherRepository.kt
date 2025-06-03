package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.VoucherFilter
import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.model.Voucher
import kotlinx.coroutines.flow.Flow

interface VoucherRepository {
    fun getVouchers(filter: VoucherFilter): Flow<PagingData<Voucher>>
    fun getVouchersForCustomer(filter: VoucherFilter): Flow<PagingData<Voucher>>
    fun createVoucher(request: VoucherRequest): Flow<ApiResponse<Voucher>>
    fun updateVoucher(id: Long, request: VoucherRequest): Flow<ApiResponse<Voucher>>
    fun deleteVoucher(id: Long): Flow<ApiResponse<Unit>>
}
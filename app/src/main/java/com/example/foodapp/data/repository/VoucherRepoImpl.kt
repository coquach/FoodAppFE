package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.VoucherFilter
import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.paging.VoucherPagingSource
import com.example.foodapp.data.remote.main_api.VoucherApi
import com.example.foodapp.domain.repository.VoucherRepository
import com.example.foodapp.domain.use_case.safeCall
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepoImpl @Inject constructor(
    private val voucherApi: VoucherApi,
) : VoucherRepository {

    override fun getVouchers(filter: VoucherFilter): Flow<PagingData<Voucher>> {

        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                VoucherPagingSource(voucherApi = voucherApi, isCustomer = false, filter = filter)
            }
        ).flow
    }

    override fun getVouchersForCustomer(filter: VoucherFilter): Flow<PagingData<Voucher>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                VoucherPagingSource(voucherApi = voucherApi, isCustomer = true, filter = filter)
            }
        ).flow
    }

    override fun createVoucher(request: VoucherRequest): Flow<ApiResponse<Voucher>> {
        return apiRequestFlow {
            voucherApi.createVouchers(request)
        }
    }

    override fun updateVoucher(
        id: Long,
        request: VoucherRequest,
    ): Flow<ApiResponse<Voucher>> {
        return apiRequestFlow {
            voucherApi.updateVoucher(id, request)
        }
    }

    override fun deleteVoucher(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            voucherApi.deleteVoucher(id)
        }
    }


}
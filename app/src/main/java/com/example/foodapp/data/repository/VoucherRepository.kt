package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.paging.OrderPagingSource
import com.example.foodapp.data.paging.VoucherPagingSource
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepository @Inject constructor(
    private val foodApi: FoodApi,
    private val accountService: AccountService
) {

    fun getVouchers(): Flow<PagingData<Voucher>> {

        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                VoucherPagingSource(foodApi = foodApi)
            }
        ).flow
    }

    fun getVouchersByCustomerId(): Flow<PagingData<Voucher>> {
        val customerId = accountService.currentUserId
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                VoucherPagingSource(foodApi = foodApi, customerId = customerId)
            }
        ).flow
    }


}
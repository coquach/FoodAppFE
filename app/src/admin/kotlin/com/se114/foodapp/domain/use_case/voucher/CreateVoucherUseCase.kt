package com.se114.foodapp.domain.use_case.voucher

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.domain.repository.VoucherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateVoucherUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository,
) {
    operator fun invoke(voucher: Voucher) = flow<ApiResponse<Voucher>> {
        emit(ApiResponse.Loading)
        try {
            val request = VoucherRequest(
                code = voucher.code,
                value = voucher.value,
                minOrderPrice = voucher.minOrderPrice,
                maxValue = voucher.maxValue,
                quantity = voucher.quantity,
                type = voucher.type,
                startDate = voucher.startDate,
                endDate = voucher.endDate,
            )
            voucherRepository.createVoucher(request).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi", 999))
        }
    }.flowOn(Dispatchers.IO)
}
package com.se114.foodapp.domain.use_case.voucher

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteVoucherUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
){
    operator fun invoke(voucherId: Long) = flow<ApiResponse<Unit>> {
        try {
            voucherRepository.deleteVoucher(voucherId).collect {
                emit(it)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi", 999))
        }
    }
}
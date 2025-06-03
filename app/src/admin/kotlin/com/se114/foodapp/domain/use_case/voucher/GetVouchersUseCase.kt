package com.se114.foodapp.domain.use_case.voucher

import com.example.foodapp.data.dto.filter.VoucherFilter
import com.example.foodapp.domain.repository.VoucherRepository
import javax.inject.Inject

class GetVouchersUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(filter: VoucherFilter) = voucherRepository.getVouchers(filter)
}
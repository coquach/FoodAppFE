package com.example.foodapp.domain.use_case.voucher

import com.example.foodapp.data.dto.filter.VoucherFilter
import com.example.foodapp.domain.repository.VoucherRepository
import javax.inject.Inject

class GetVoucherForCustomerUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
){
    operator fun invoke(filter: VoucherFilter) = voucherRepository.getVouchersForCustomer(filter)
}
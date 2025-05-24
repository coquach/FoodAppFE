package com.se114.foodapp.domain.use_case.voucher

import com.example.foodapp.domain.repository.VoucherRepository
import javax.inject.Inject

class GetVoucherForCustomerUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
){
    operator fun invoke() = voucherRepository.getVouchersForCustomer()
}
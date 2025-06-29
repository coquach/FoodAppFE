package com.example.foodapp.domain.use_case.order

import android.util.Log
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutUiModel
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.domain.repository.OrderRepository
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(
        checkout: CheckoutUiModel,
        cartItems: List<CartItem>,
        customerId: String? = null,
        shipperId: String? = null,
        sellerId: String? = null,
    ) = flow<ApiResponse<Order>> {
        try {
            emit(ApiResponse.Loading)
            val now = StringUtils.getFormattedCurrentVietnamDateTime()
            val request = OrderRequest(
                foodTableId = checkout.foodTableNumber,
                voucherId = checkout.voucher?.id,
                type = checkout.type,
                method = PaymentMethod.fromDisplay(checkout.method)!!.name,
                startedAt = now,
                paymentAt = now,
                note = checkout.note,
                address = checkout.address,
                orderItems = cartItems.map { cartItem ->
                    OrderItemRequest(
                        foodId = cartItem.id,
                        quantity = cartItem.quantity,
                    )
                },
                status = checkout.status,
                phone = checkout.phone,
                sellerId = sellerId,
                shipperId = shipperId,
                customerId = customerId,
            )
            
            Log.d("PlaceOrderUseCase", "request: $request")
            Log.d("PlaceOrderUseCase", "request: ${checkout.address}")
            orderRepository.createOrder(request).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi đặt hàng", 999))
        }
    }.flowOn(Dispatchers.IO)
}
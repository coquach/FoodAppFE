package com.se114.foodapp.utils


import androidx.compose.ui.graphics.Color

object OrdersUtils {

    enum class OrderStatus {
        PENDING_ACCEPTANCE, // Initial state when order is placed
        ACCEPTED,          // Restaurant accepted the order
        PREPARING,         // Food is being prepared
        READY,
        ASSIGNED,         // Rider assigned
        OUT_FOR_DELIVERY, // Rider picked up
        DELIVERED,        // Order completed
        REJECTED,         // Restaurant rejected the order
        CANCELLED,         // Customer cancelled
        DEFAULT
    }

    fun getOrderStatusFromString(status: String): OrderStatus? {
        return try {

            OrderStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getOrderStatusInVietnamese(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING_ACCEPTANCE -> "Đang chờ"
            OrderStatus.ACCEPTED -> "Đã chấp nhận"
            OrderStatus.PREPARING -> "Đang chuẩn bị"
            OrderStatus.READY -> "Sẵn sàng"
            OrderStatus.ASSIGNED -> "Đã giao cho nhân viên"
            OrderStatus.OUT_FOR_DELIVERY -> "Đang giao hàng"
            OrderStatus.DELIVERED -> "Đã giao"
            OrderStatus.REJECTED -> "Đã từ chối"
            OrderStatus.CANCELLED -> "Đã hủy"
            else -> "Không xác định"
        }
    }

    fun getStatusColor(status: OrderStatus): Color {
        return when (status) {
            OrderStatus.PENDING_ACCEPTANCE -> Color(0xFF2196F3) // Xanh dương
            OrderStatus.ACCEPTED -> Color(0xFF8BC34A) // Xanh lá
            OrderStatus.CANCELLED -> Color(0xFFF44336) // Đỏ
            OrderStatus.DELIVERED -> Color(0xFFFF9800) // Cam
            OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF9C27B0) // Tím
            else -> Color(0xFF9E9E9E) // Màu xám cho trạng thái mặc định
        }
    }
}

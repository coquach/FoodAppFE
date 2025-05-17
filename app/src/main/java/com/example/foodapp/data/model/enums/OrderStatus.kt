package com.example.foodapp.data.model.enums


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.foodapp.R

enum class OrderStatus(val display: String, val color: Color,
                       val icon: ImageVector
) {
    PENDING("Đang chờ",  Color(0xFF2196F3), Icons.Default.Schedule),
    CONFIRMED("Đã xác nhận", Color(0xFF42A5F5), Icons.Default.CheckCircle),
    READY("Đã sẵn sàng", Color(0xFFFF9800), Icons.Default.PendingActions),
    SHIPPING("Đã vận chuyển",   Color(0xFFAB47BC), Icons.Default.LocalShipping),
    COMPLETED("Đã hoàn thành", Color(0xFF2E7D32), Icons.Default.DoneAll),
    CANCELLED("Đã hủy", Color(0xFFE57373), Icons.Default.Cancel);


    override fun toString(): String = name

    companion object {
        fun fromDisplay(display: String): OrderStatus? {
            return OrderStatus.entries.firstOrNull { it.display == display }
        }
        fun fromName(name: String): OrderStatus? {
            return OrderStatus.entries.firstOrNull { it.name == name }
        }
    }
}
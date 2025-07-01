package com.example.foodapp.ui.screen.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.VoucherType
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.utils.StringUtils
import java.math.BigDecimal


@Composable
fun PerforatedEdge(
    dotColor: Color = MaterialTheme.colorScheme.background,
    dotRadius: Dp = 4.dp,
    spacing: Dp = 8.dp,
) {
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(16.dp),
    ) {
        val canvasHeight = size.height
        val dotDiameter = dotRadius.toPx() * 2 + spacing.toPx()
        val totalDots = (canvasHeight / dotDiameter).toInt()
        val usedHeight = totalDots * dotDiameter - spacing.toPx()
        val verticalPadding = (canvasHeight - usedHeight) / 2
        val centerX = size.width / 2
        var currentY = verticalPadding

        repeat(totalDots) {
            drawCircle(
                color = dotColor,
                radius = dotRadius.toPx(),
                center = Offset(centerX, currentY + dotRadius.toPx())
            )
            currentY += dotDiameter
        }
    }
}
//@Composable
//fun ZigzagEdge(
//    modifier: Modifier = Modifier,
//    isLeft: Boolean = true,
//    zigzagSize: Dp = 8.dp,
//    zigzagCount: Int = 10,
//    color: Color = Color.White
//) {
//    Canvas(modifier = modifier) {
//        val path = Path()
//        val zigzagHeight = size.height / zigzagCount
//        val zigzagWidth = zigzagSize.toPx()
//
//        for (i in 0 until zigzagCount) {
//            val yStart = i * zigzagHeight
//            val yMid = yStart + zigzagHeight / 2
//            val yEnd = yStart + zigzagHeight
//
//            if (isLeft) {
//                path.moveTo(size.width, yStart)
//                path.lineTo(size.width - zigzagWidth, yMid)
//                path.lineTo(size.width, yEnd)
//            } else {
//                path.moveTo(0f, yStart)
//                path.lineTo(zigzagWidth, yMid)
//                path.lineTo(0f, yEnd)
//            }
//        }
//
//        drawPath(path = path, color = color)
//    }
//}

@Composable
fun VoucherCard(
    modifier: Modifier = Modifier,
    voucher: Voucher,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier.height(170.dp)) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .fillMaxSize()
                .padding(2.dp)
                .clickable {
                    onClick?.invoke()
                },


            ) {

            PerforatedEdge()
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailsTextRow(
                        text = "Voucher: ${voucher.code}",
                        icon = Icons.Default.Tag,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    DetailsTextRow(
                        text = "Đơn tối thiểu: ${StringUtils.formatCurrency(voucher.minOrderPrice)}",
                        icon = Icons.Default.MonetizationOn,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    DetailsTextRow(
                        text = "Giá trị tối đa: ${StringUtils.formatCurrency(voucher.maxValue)} ",
                        icon = Icons.Default.MonetizationOn,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    DetailsTextRow(
                        text = "Ngày bắt đầu: ${StringUtils.formatLocalDate(voucher.startDate)}",
                        icon = Icons.Default.MonetizationOn,
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                    DetailsTextRow(
                        text = "Ngày kết thúc: ${StringUtils.formatLocalDate(voucher.endDate)}",
                        icon = Icons.Default.MonetizationOn,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailsTextRow(
                            text = "SL: ${voucher.quantity}",
                            icon = Icons.Default.Numbers,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        if(voucher.type == VoucherType.PERCENTAGE.name){
                            Text(
                                text = "${voucher.value} %",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        } else if(voucher.type == VoucherType.AMOUNT.name){
                            Text(
                                text = "${StringUtils.formatCurrency(BigDecimal(voucher.value))} ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                }
            }

            PerforatedEdge()
        }
        if (voucher.expired) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // lớp mờ tối
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ĐÃ HẾT HẠN",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}



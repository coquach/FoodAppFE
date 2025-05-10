package com.example.foodapp.ui.screen.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.utils.StringUtils
import java.math.BigDecimal

@Composable
fun CheckoutDetailsView(
    checkoutDetails: CheckoutDetails,
    voucherValue: BigDecimal = BigDecimal(0)
) {
    Column {
        CheckoutRowItem(
            title = "Tổng giá", value = checkoutDetails.subTotal
        )
        CheckoutRowItem(
            title = "Thuế GTGT", value = checkoutDetails.tax
        )
        CheckoutRowItem(
            title = "Phí ship", value = checkoutDetails.deliveryFee
        )
        CheckoutRowItem(
            title = "Voucher", value = voucherValue
        )
        CheckoutRowItem(
            title = "Tổng cộng", value = checkoutDetails.totalAmount + voucherValue, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckoutRowItem(title: String, value: BigDecimal, fontWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

        ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = fontWeight
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = StringUtils.formatCurrency(value),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontWeight = fontWeight
        )
    }

}

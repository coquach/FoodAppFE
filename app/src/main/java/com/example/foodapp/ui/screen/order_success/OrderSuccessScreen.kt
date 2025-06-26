package com.example.foodapp.ui.screen.order_success

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.OrderList

@Composable
fun OrderSuccessScreen(orderID: Long, navController: NavController) {
    BackHandler {
        navController.popBackStack(route = Home, inclusive = false)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.order_success),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
        Text(text = "Đặt hàng thành công", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Mã đơn hàng: $orderID",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            navController.navigate(OrderList) {
                popUpTo(Home) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = "Đơn hàng ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            navController.navigate(Home) {
                popUpTo(Home) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Text(text = "Trở về", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}
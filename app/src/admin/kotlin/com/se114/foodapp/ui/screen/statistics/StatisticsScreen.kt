package com.se114.foodapp.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.navigation.Notification
import com.example.foodapp.ui.screen.components.ItemCount
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.screen.components.charts.LineChartSample
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StatisticsScreen(
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val unReadCount by notificationViewModel.unreadCount.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                StatisticsViewModel.StatisticsEvents.NavigateToDetail -> {

                }

                StatisticsViewModel.StatisticsEvents.NavigateToNotification -> {
                    navController.navigate(Notification)
                }

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(50.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nofication),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Center)
                        .clickable {
                            viewModel.onNotificationClicked()
                        }
                )

                if (unReadCount > 0) {
                    ItemCount(unReadCount)
                }

            }
        }
        OrderCard(1, {})

        TabWithPager(
            tabs = listOf("Doanh thu", "Chi tiêu"),
            pages = listOf(
                {
                    Column {
                        Spacer(modifier = Modifier.size(16.dp))
                        LineChartSample()
                    }

                },
                {
                    Column {
                        Spacer(modifier = Modifier.size(16.dp))
                        LineChartSample()
                    }
                }
            ),
            onTabSelected = {}
        )
    }
}

@Composable
fun OrderCard(orderQuantity: Int, onClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
            .clickable {
                onClicked.invoke()
            }
            .padding(16.dp)

    ) {
        Row {
            Icon(
                imageVector = Icons.Filled.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

         Text(
             text = "Tổng đơn hàng: $orderQuantity",
             color = MaterialTheme.colorScheme.onTertiary

         )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewCard() {
    FoodAppTheme {
        OrderCard(2, {})
    }
}

package com.se114.foodapp.ui.screen.statistics

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.navigation.Notification
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.screen.components.charts.BarChartFromEntries
import com.example.foodapp.ui.screen.components.charts.ChartEntry
import com.example.foodapp.ui.screen.components.charts.DonutChatSample
import com.example.foodapp.ui.screen.components.charts.LineChartFromEntries
import com.example.foodapp.ui.screen.components.charts.MenuDonutSlice
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.ui.component.MonthPicker

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                StaticsState.Event.GoToNotification -> {
                    navController.navigate(Notification)
                }

                is StaticsState.Event.ShowErrorToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getMonthlyReports()
        viewModel.getDailyReports()
        viewModel.getMenuReports()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
       HeaderDefaultView(text = "Thống kê")


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MonthSection(
                    month = uiState.fromMonth,
                    year = uiState.fromYear,
                    onClicked = { month, year ->
                        viewModel.onAction(StaticsState.Action.OnChangeFromMonthYear(year, month))

                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                MonthSection(
                    month = uiState.toMonth,
                    year = uiState.toYear,
                    onClicked = { month, year ->
                        viewModel.onAction(StaticsState.Action.OnChangeToMonthYear(year, month))
                    }
                )
            }
            TabWithPager(
                tabs = listOf("Doanh thu", "Lợi nhuận", "Lương", "Nhập kho"),
                pages = listOf(
                    {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)) {
                            when (uiState.monthlyReportState) {
                                is StaticsState.MonthlyReportState.Error -> {
                                    Retry(
                                        modifier = Modifier.fillMaxSize(),
                                        onClicked = {
                                            viewModel.onAction(StaticsState.Action.GetMonthlyReport)
                                        },
                                        message = (uiState.monthlyReportState as StaticsState.MonthlyReportState.Error).message,
                                    )
                                }

                                StaticsState.MonthlyReportState.Loading -> {
                                    Loading(
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                StaticsState.MonthlyReportState.Success -> {
                                    if(uiState.monthlyReports.isEmpty()){
                                        Nothing(
                                            modifier = Modifier.fillMaxSize(),
                                            icon = Icons.Default.BarChart,
                                            text = "Chưa có thống kê tháng"


                                        )
                                    }else {
                                        val totalSales = uiState.monthlyReports.map {
                                            ChartEntry(
                                                xLabel = StringUtils.formatLocalDate(it.reportMonth)!!,
                                                value = it.totalSales,
                                            )
                                        }
                                        LineChartFromEntries(
                                            entries = totalSales,
                                        )
                                    }

                                }
                            }
                        }


                    },
                    {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)) {
                            when (uiState.monthlyReportState) {
                                is StaticsState.MonthlyReportState.Error -> {
                                    Retry(
                                        modifier = Modifier.fillMaxSize(),
                                        onClicked = {
                                            viewModel.onAction(StaticsState.Action.GetMonthlyReport)
                                        },
                                        message = (uiState.monthlyReportState as StaticsState.MonthlyReportState.Error).message,
                                    )
                                }

                                StaticsState.MonthlyReportState.Loading -> {
                                    Loading(
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                StaticsState.MonthlyReportState.Success -> {
                                    if(uiState.monthlyReports.isEmpty()){
                                        Nothing(
                                            modifier = Modifier.fillMaxSize(),
                                            icon = Icons.Default.BarChart,
                                            text = "Chưa có thống kê tháng"


                                        )
                                    } else {
                                        val netProfits = uiState.monthlyReports.map {
                                            ChartEntry(
                                                xLabel = StringUtils.formatLocalDate(it.reportMonth)!!,
                                                value = it.netProfit,
                                            )
                                        }
                                        LineChartFromEntries(
                                            entries = netProfits,
                                        )
                                    }

                                }
                            }
                        }

                    },
                    {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)) {
                            when (uiState.monthlyReportState) {
                                is StaticsState.MonthlyReportState.Error -> {
                                    Retry(
                                        modifier = Modifier.fillMaxSize(),
                                        onClicked = {
                                            viewModel.onAction(StaticsState.Action.GetMonthlyReport)
                                        },
                                        message = (uiState.monthlyReportState as StaticsState.MonthlyReportState.Error).message,
                                    )
                                }

                                StaticsState.MonthlyReportState.Loading -> {
                                    Loading(
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                StaticsState.MonthlyReportState.Success -> {
                                    if(uiState.monthlyReports.isEmpty()){
                                        Nothing(
                                            modifier = Modifier.fillMaxSize(),
                                            icon = Icons.Default.BarChart,
                                            text = "Chưa có thống kê tháng"


                                        )
                                    }else {
                                        val totalImportCosts = uiState.monthlyReports.map {
                                            ChartEntry(
                                                xLabel = StringUtils.formatLocalDate(it.reportMonth)!!,
                                                value = it.totalImportCost,
                                            )
                                        }
                                        LineChartFromEntries(
                                            entries = totalImportCosts,
                                        )
                                    }

                                }
                            }
                        }

                    },
                    {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)) {
                            when (uiState.monthlyReportState) {
                                is StaticsState.MonthlyReportState.Error -> {
                                    Retry(
                                        modifier = Modifier.fillMaxSize(),
                                        onClicked = {
                                            viewModel.onAction(StaticsState.Action.GetMonthlyReport)
                                        },
                                        message = (uiState.monthlyReportState as StaticsState.MonthlyReportState.Error).message,
                                    )
                                }

                                StaticsState.MonthlyReportState.Loading -> {
                                    Loading(
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                StaticsState.MonthlyReportState.Success -> {
                                    if(uiState.monthlyReports.isEmpty()){
                                        Nothing(
                                            modifier = Modifier.fillMaxSize(),
                                            icon = Icons.Default.BarChart,
                                            text = "Chưa có thống kê tháng"


                                        )
                                    }else {
                                        val totalSalaries = uiState.monthlyReports.map {
                                            ChartEntry(
                                                xLabel = StringUtils.formatLocalDate(it.reportMonth)!!,
                                                value = it.totalSalaries,
                                            )
                                        }
                                        LineChartFromEntries(
                                            entries = totalSalaries,
                                        )
                                    }

                                }
                            }
                        }

                    },
                ),
                scrollable = true,
                onTabSelected = {},
                modifier = Modifier.fillMaxWidth()

            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                IconButton(
                    onClick = {
                        viewModel.onAction(
                            StaticsState.Action.OnChangeSelectedMonthYear(

                                uiState.selectedYear,
                                uiState.selectedMonth - 1,
                            )
                        )
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = Color.Transparent),

                    ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = "Left",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                MonthSection(
                    month = uiState.selectedMonth,
                    year = uiState.selectedYear,
                    onClicked = { month, year ->
                        viewModel.onAction(
                            StaticsState.Action.OnChangeSelectedMonthYear(
                                year,
                                month
                            )
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        viewModel.onAction(
                            StaticsState.Action.OnChangeSelectedMonthYear(

                                uiState.selectedYear,
                                uiState.selectedMonth + 1,
                            )
                        )
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color = Color.Transparent),

                    ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Left",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            TabWithPager(
                tabs = listOf("Doanh thu", "Số đơn"),
                pages = listOf(
                    {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)) {
                            when (uiState.dailyReportState) {
                                is StaticsState.DailyReportState.Error -> {
                                    Retry(
                                        modifier = Modifier.fillMaxSize(),
                                        onClicked = {
                                            viewModel.onAction(StaticsState.Action.GetDailyReport)
                                        },
                                        message = (uiState.dailyReportState as StaticsState.DailyReportState.Error).message,
                                    )
                                }

                                StaticsState.DailyReportState.Loading -> {
                                    Loading(
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                StaticsState.DailyReportState.Success -> {
                                    if(uiState.dailyReports.isEmpty()){
                                        Nothing(
                                            modifier = Modifier.fillMaxSize(),
                                            icon = Icons.Default.BarChart,
                                            text = "Chưa có thống kê ngày")
                                    } else {
                                        val totalSales = uiState.dailyReports.map {
                                            ChartEntry(
                                                xLabel = StringUtils.formatLocalDate(it.reportDate)!!,
                                                value = it.totalSales,
                                            )
                                        }
                                        BarChartFromEntries(
                                            entries = totalSales,
                                        )
                                    }

                                }
                            }
                        }


                    },
                    {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)) {
                            when (uiState.dailyReportState) {
                                is StaticsState.DailyReportState.Error -> {
                                    Retry(
                                        modifier = Modifier.fillMaxSize(),
                                        onClicked = {
                                            viewModel.onAction(StaticsState.Action.GetDailyReport)
                                        },
                                        message = (uiState.dailyReportState as StaticsState.DailyReportState.Error).message,
                                    )
                                }

                                StaticsState.DailyReportState.Loading -> {
                                    Loading(
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }

                                StaticsState.DailyReportState.Success -> {
                                    if(uiState.dailyReports.isEmpty()){
                                        Nothing(
                                            modifier = Modifier.fillMaxSize(),
                                            icon = Icons.Default.BarChart,
                                            text = "Chưa có thống kê ngày")
                                    } else {
                                        val totalOrders = uiState.dailyReports.map {
                                            ChartEntry(
                                                xLabel = StringUtils.formatLocalDate(it.reportDate)!!,
                                                value = it.totalOrders,
                                            )
                                        }
                                        BarChartFromEntries(
                                            entries = totalOrders,
                                        )
                                    }

                                }
                            }

                        }

                    },

                    ),
                onTabSelected = {},
                modifier = Modifier.fillMaxWidth()

            )
            Text(
                text = "Thực đơn bán chạy nhất",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)) {
                when (uiState.menuReportState) {
                    is StaticsState.MenuReportState.Error -> {
                        Retry(
                            modifier = Modifier.fillMaxSize(),
                            onClicked = {
                                viewModel.onAction(StaticsState.Action.GetMenuReport)
                            },
                            message = (uiState.menuReportState as StaticsState.MenuReportState.Error).message,
                        )
                    }

                    StaticsState.MenuReportState.Loading -> {
                        Loading(
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    StaticsState.MenuReportState.Success -> {
                        if(uiState.menuReports.isEmpty()){
                            Nothing(
                                modifier = Modifier.fillMaxSize(),
                                icon = Icons.Default.Category,
                                text = "Chưa có thống kê thực đơn"
                            )
                        } else {
                            val donutDates = uiState.menuReports.map {
                                MenuDonutSlice(
                                    name = it.menuName,
                                    value = it.percentage,
                                )
                            }

                            DonutChatSample(
                                data = donutDates,
                                extractLabel = { it.name },
                                extractValue = { it.value },
                                modifier = Modifier.fillMaxSize()
                                )
                        }

                    }
                }

            }

        }


    }
}


@Composable
fun MonthSection(
    modifier: Modifier = Modifier,
    month: Int,
    year: Int,
    onClicked: (Int, Int) -> Unit,

    ) {
    var showDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .width(127.dp)
            .height(37.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                showDialog = true
            },
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Month Selected",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "$month/$year",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

    }
    if (showDialog) {
        MonthPicker(
            currentMonth = month - 1,
            currentYear = year,
            confirmButtonCLicked = onClicked,
            cancelClicked = {
                showDialog = false
            }
        )
    }
}

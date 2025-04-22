package com.example.foodapp.ui.screen.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart

import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.foodapp.ui.theme.FoodAppTheme

@Composable
fun DonutChatSample(){
    val slices = listOf(
        PieChartData.Slice("HP", 20f, Color(0xFF5F0A87)),
        PieChartData.Slice("Dell", 30f, Color(0xFF20BF55)),
        PieChartData.Slice("Lenovo", 40f, Color(0xFFEC9F05)),
        PieChartData.Slice("Asus", 10f, Color(0xFFF53844))
    )
    val donutChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Donut,
    )
    val donutChartConfig = PieChartConfig(
        labelColor = Color.Black,
        labelColorType = PieChartConfig.LabelColorType.SLICE_COLOR,
        backgroundColor = Color.Transparent,
        strokeWidth = 60f,
        activeSliceAlpha = 0.8f,
        isAnimationEnable = true,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            pieChartData = donutChartData,
            pieChartConfig = donutChartConfig
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Custom legend
        slices.forEach { slice ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(slice.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${slice.label}: ${slice.value}%", color = Color.Black)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewDonutChart(){
    FoodAppTheme {
        DonutChatSample()
    }
}
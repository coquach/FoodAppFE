package com.example.foodapp.ui.screen.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import java.math.BigDecimal
import kotlin.random.Random

@Composable
fun<T> DonutChatSample(
    data: List<T>,
    extractLabel: (T) -> String,
    extractValue: (T) -> Float,
    modifier: Modifier = Modifier,

    strokeWidth: Float = 200f,
    labelColor: Color = Color.Black
){
    val slices = data.map {
        PieChartData.Slice(
            label = extractLabel(it),
            value = extractValue(it),
            color = randomColor()
        )
    }

    val donutChartData = PieChartData(
        slices = slices,
        plotType = PlotType.Donut
    )

    val donutChartConfig = PieChartConfig(
        labelColor = labelColor,
        labelColorType = PieChartConfig.LabelColorType.SLICE_COLOR,
        backgroundColor = Color.Transparent,
        strokeWidth = strokeWidth,
        activeSliceAlpha = 0.8f,
        isAnimationEnable = true,
    )

    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {


        DonutPieChart(
            modifier = Modifier.fillMaxWidth().padding(30.dp),
            pieChartData = donutChartData,
            pieChartConfig = donutChartConfig
        )


        // Custom legend
        slices.forEach { slice ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(slice.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${slice.label}: ${String.format("%.2f", slice.value)}%", color = labelColor)
            }
        }
    }
}

data class MenuDonutSlice(val name: String, val value: Float)

fun randomColor(): Color {
    // Generate a random RGB color with decent brightness
    val r = Random.nextInt(100, 256)
    val g = Random.nextInt(100, 256)
    val b = Random.nextInt(100, 256)
    return Color(r, g, b)
}


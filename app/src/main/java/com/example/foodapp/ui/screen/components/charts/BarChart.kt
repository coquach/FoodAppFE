package com.example.foodapp.ui.screen.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData

@Composable
fun <T> BarChartFromEntries(
    modifier: Modifier = Modifier,
    entries: List<ChartEntry<T>>,
    maxY: Float = entries.maxOfOrNull { it.toFloat(it.value) } ?: 100f,
    yStepSize: Int = 5,


    ) {
    val paddedMaxY = (maxY * 1.1f).let {
        if (it == 0f) 100f else it
    }

    // Chuyển entries thành Bar list
    val barChartList = entries.mapIndexed { index, entry ->
        BarData(
            point = Point(index.toFloat(), entry.toFloat(entry.value)),
            color = MaterialTheme.colorScheme.primary
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(20.dp)
        .steps(barChartList.size - 1)
        .bottomPadding(40.dp)
        .axisLabelAngle(20f)
        .labelData { index -> entries.getOrNull(index)?.xLabel ?: "" }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index ->
            val step = paddedMaxY / yStepSize
            (index * step).toInt().toString()
        }
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val barChartData = BarChartData(
        chartData = barChartList,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )


    BarChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        barChartData = barChartData
    )


}

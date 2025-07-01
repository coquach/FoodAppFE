package com.example.foodapp.ui.screen.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.foodapp.ui.theme.FoodAppTheme

@Composable
fun <T> LineChartFromEntries(
    modifier: Modifier = Modifier,
    entries: List<ChartEntry<T>>,
    maxY: Float = entries.maxOfOrNull { it.toFloat(it.value) }  ?: 100f,
    steps: Int = 5,

) {

    val paddedMaxY = (maxY * 1.1f).let {
        if (it == 0f) 100f else it // đề phòng tất cả đều là 0
    }
    val pointsData = entries.mapIndexed { index, entry ->
        Point(index.toFloat(), entry.toFloat(entry.value))
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(80.dp)
        .backgroundColor(Color.Transparent)
        .steps(entries.size - 1)
        .labelData { i -> entries.getOrNull(i)?.xLabel ?: "" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(25.dp)
        .labelData { i ->
            val step = paddedMaxY / steps
            ((i * step).toInt()).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outline),
        backgroundColor = MaterialTheme.colorScheme.surface,
    )

    LineChart(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        lineChartData = lineChartData
    )
}


data class ChartEntry<T>(
    val xLabel: String,
    val value: T,
    val toFloat: (T) -> Float = { (it as Number).toFloat() }, // fallback default
    val labelFormatter: ((T) -> String)? = null // optional tooltip format
)

package com.example.foodapp.ui.screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSample(
    modifier: Modifier = Modifier,
    onDateRangeSelected: (LocalDate?, LocalDate?) -> Unit // Callback để trả lại ngày đã chọn
) {
    var showDialog by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    // State lưu trữ ngày bắt đầu và ngày kết thúc
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }


    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        selectedStartDate = selectedStartDate ?: today
        selectedEndDate = selectedEndDate ?: today
    }

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDialog = true
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TextField cho ngày bắt đầu
        FoodAppTextField(
            value = selectedStartDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
            onValueChange = {},
            readOnly = true,
            labelText = "Ngày bắt đầu",
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            },
            interactionSource = interactionSource,
            modifier = Modifier
                .clickable { showDialog = true }
                .width(200.dp)
        )

        // TextField cho ngày kết thúc
        FoodAppTextField(
            value = selectedEndDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
            onValueChange = {},
            readOnly = true,
            labelText = "Ngày kết thúc",
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            },
            interactionSource = interactionSource,
            modifier = Modifier
                .clickable { showDialog = true }
                .width(200.dp)
        )
    }

    // Dialog chọn phạm vi ngày
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {

                        val startDate = dateRangePickerState.selectedStartDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }


                        selectedStartDate = startDate
                        selectedEndDate = endDate


                        onDateRangeSelected(startDate, endDate)


                        showDialog = false
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy")
                }
            }
        ) {

            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Chọn ngày",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                headline = {
                    val startDate = dateRangePickerState.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }

                    val endDate = dateRangePickerState.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }

                    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = startDate?.format(dateFormatter) ?: "__/__/____",

                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Text(
                            text = "  →  ",
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        Text(
                            text = endDate?.format(dateFormatter) ?: "__/__/____",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                },
            )
        }
    }
}
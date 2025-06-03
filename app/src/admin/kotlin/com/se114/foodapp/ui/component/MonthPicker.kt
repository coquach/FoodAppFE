package com.se114.foodapp.ui.component


import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment



@Composable
fun MonthPicker(
    currentMonth: Int,
    currentYear: Int,
    confirmButtonCLicked: (Int, Int) -> Unit,
    cancelClicked: () -> Unit
) {

    val months = listOf(
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC"
    )

    var month by remember {
        mutableStateOf(months[currentMonth])
    }

    var year by remember {
        mutableIntStateOf(currentYear)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }
    AlertDialog(
        onDismissRequest = cancelClicked,
        title = {
            Text(
                text = "Chọn thời gian",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = { Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                year--
                            }
                        ),
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = year.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = {
                                year++
                            }
                        ),
                    imageVector =  Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null
                )

            }


            Card (
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),

            ) {

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 16.dp,
                    crossAxisSpacing = 16.dp,
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = FlowCrossAxisAlignment.Center
                ) {

                    months.forEach {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = interactionSource,
                                    onClick = {
                                        month = it
                                    }
                                )
                                .background(
                                    color = Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            val animatedSize by animateDpAsState(
                                targetValue = if (month == it) 60.dp else 0.dp,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = LinearOutSlowInEasing
                                )
                            )

                            Box(
                                modifier = Modifier.size(animatedSize)
                                    .background(
                                        color = if (month == it) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )

                            Text(
                                text = it,
                                color = if (month == it) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Medium
                            )

                        }
                    }

                }

            }

        } },
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = {

                Button(
                    modifier = Modifier.padding(end = 20.dp),
                        onClick = {
                            confirmButtonCLicked(
                                months.indexOf(month) + 1,
                                year
                            )
                            cancelClicked()
                        },
                        shape = CircleShape,

                        colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primary)

                ) {
                    Text(
                            text = "Chọn",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                }

        },
        dismissButton = {
            Button(modifier = Modifier.padding(end = 20.dp),
                onClick = {
                    cancelClicked()
                },
                shape = CircleShape,
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
            ) {
                Text(
                    text = "Đóng",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )










}


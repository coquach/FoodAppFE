package com.se114.foodapp.ui.screen.export

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Export
import com.example.foodapp.navigation.ExportDetails
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.utils.StringUtils
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    navController: NavController,
    viewModel: ExportViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exports = remember(uiState.filter) {
        viewModel.getExports(uiState.filter)
    }.collectAsLazyPagingItems()


    var showDialogDelete by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }
    var isDeletable by rememberSaveable { mutableStateOf(false) }


    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        val condition = handle?.get<Boolean>("updated") == true
        if (condition) {
            handle["updated"] = false
            exports.refresh()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                ExportState.Event.AddExport -> {
                    navController.navigate(
                        ExportDetails(
                            export = Export(exportDate = LocalDate.now()),
                            isUpdating = false
                        )
                    )
                }

                is ExportState.Event.GoToDetail -> {
                    navController.navigate(ExportDetails(it.export, true))
                }

                ExportState.Event.NotifyCantDelete -> {
                    Toast.makeText(
                        context,
                        "Không thể xóa phiếu xuất vì đã quá hạn 1 ngày",
                        Toast.LENGTH_SHORT
                    )
                }


                ExportState.Event.ShowError -> {
                    showErrorSheet = true
                }

                is ExportState.Event.ShowSuccessToast -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(ExportState.Action.AddExport)
                    },
                    bgColor = MaterialTheme.colorScheme.primary,
                ) {
                    Box(modifier = Modifier.size(56.dp)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Center)
                                .size(35.dp)
                        )
                    }
                }
            }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {

            HeaderDefaultView(
                text = "Phiếu xuất hàng",

                )
            DateRangePickerSample(
                modifier = Modifier.width(170.dp),
                startDateText = "Bắt đầu",
                endDateText = "Kết thúc",
                startDate = uiState.filter.startDate,
                endDate = uiState.filter.endDate,
                onDateRangeSelected = { startDate, endDate ->
                    viewModel.onAction(ExportState.Action.OnDateChange(startDate, endDate))
                }
            )

            LazyPagingSample(
                onRefresh = {
                    viewModel.onAction(ExportState.Action.OnRefresh)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                items = exports,
                textNothing = "Không có phiếu xuất nào",
                iconNothing = Icons.Default.Description,
                columns = 1,
                key = {
                    it.id!!
                },
                itemContent = {
                    SwipeableActionsBox(
                        modifier = Modifier
                            .padding(
                                8.dp,
                            )
                            .clip(RoundedCornerShape(12.dp)),
                        endActions = listOf(
                            SwipeAction(
                                icon = rememberVectorPainter(Icons.Default.Delete),
                                background = MaterialTheme.colorScheme.error,
                                onSwipe = {
                                    isDeletable =
                                        it.exportDate?.plusDays(1)?.isAfter(LocalDate.now()) == true
                                    if (isDeletable) {
                                        viewModel.onAction(ExportState.Action.OnExportSelected(it))
                                        showDialogDelete = true
                                    } else {
                                        viewModel.onAction(ExportState.Action.NotifyCantDelete)
                                    }
                                }
                            ))
                    ) {
                        ExportCard(
                            export = it,
                            onClick = {
                                viewModel.onAction(ExportState.Action.OnExportClicked(it))
                            }
                        )
                    }
                }
            )
        }


    }

    if (showDialogDelete) {

        FoodAppDialog(
            title = "Xóa phiếu xuất",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa phiếu đã chọn không?",
            onDismiss = {

                showDialogDelete = false
            },
            onConfirm = {
                viewModel.onAction(ExportState.Action.DeleteExport)
                showDialogDelete = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            }
        )
    }
}

@Composable
fun ExportDetail(export: Export) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,

            ) {

            Icon(
                imageVector = Icons.Default.ImportExport,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {


                DetailsTextRow(
                    text = "${export.id}",
                    icon = Icons.Default.Tag,
                    color = MaterialTheme.colorScheme.outline
                )

                DetailsTextRow(
                    text = StringUtils.formatLocalDate(export.exportDate) ?: "",
                    icon = Icons.Default.DateRange,
                    color = MaterialTheme.colorScheme.outline
                )
            }


        }


    }
}

@Composable
fun ExportCard(export: Export, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            ExportDetail(export = export)
            Button(onClick = onClick) {
                Text(
                    text = "Chi tiết",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

        }
    }

}



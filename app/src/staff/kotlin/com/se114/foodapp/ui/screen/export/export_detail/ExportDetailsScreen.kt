package com.se114.foodapp.ui.screen.export.export_detail

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Inventory

import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.ui.theme.confirm
import com.example.foodapp.ui.theme.onConfirm
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.math.BigDecimal

@Composable
fun ExportDetailsScreen(
    navController: NavController,
    viewModel: ExportDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inventories = viewModel.inventories.collectAsLazyPagingItems()


    var isCreating by rememberSaveable { mutableStateOf(false) }
    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }




    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isInventoryDialog by rememberSaveable { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect { event ->
            when (event) {
                ExportDetailsState.Event.GoBack -> {
                    navController.popBackStack()
                }
                ExportDetailsState.Event.ShowError -> {
                   showErrorSheet = true
                    }
                ExportDetailsState.Event.BackToAfterModify -> {
                    if (uiState.isUpdating) {
                        Toast.makeText(
                            navController.context,
                            "Cập nhật đơn xuất thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            navController.context,
                            "Tạo đơn xuất thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    navController.previousBackStackEntry?.savedStateHandle?.set("updated", true)
                    navController.popBackStack()
                }

                ExportDetailsState.Event.NotifyCantEdit -> {
                    Toast.makeText(
                        navController.context,
                        "Không thể sửa đơn xuất vì quá hạn 1 ngày",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(ExportDetailsState.Action.OnBack)
            },
            text = "Thông tin đơn xuất"
        )


        if (uiState.exportDetails.isEmpty() && !isCreating) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {
                            viewModel.onAction(ExportDetailsState.Action.OnExportDetailsSelected(ExportDetailUIModel()))
                            isCreating = true
                            isInventoryDialog = true
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.outline)
                            .padding(0.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Export",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Thêm chi tiết đơn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState

                ) {
                    item {
                        AnimatedContent(
                            targetState = isCreating,
                            transitionSpec = {
                                (slideInVertically { it } + fadeIn()) togetherWith
                                        (slideOutVertically { -it } + fadeOut())
                            },
                            label = "Creating Switch"){creating ->
                            if (creating) {
                                ExportDetailsCard(
                                    exportDetail = uiState.exportDetailsSelected,
                                    onIncrement = {
                                        viewModel.onAction(ExportDetailsState.Action.OnChangeQuantity(uiState.exportDetailsSelected.quantity + BigDecimal.ONE))
                                    },
                                    onDecrement = {
                                        viewModel.onAction(ExportDetailsState.Action.OnChangeQuantity(uiState.exportDetailsSelected.quantity - BigDecimal.ONE))
                                    },
                                    onUpdate = {
                                        viewModel.onAction(ExportDetailsState.Action.AddExportDetails)
                                        isCreating = false
                                    },
                                    onClose = { isCreating = false },
                                    isUpdating = false
                                )
                            }

                        }

                    }
                    items(uiState.exportDetails, key = { exportDetails -> exportDetails.localId }) { exportDetails ->
                        val isEditing = isEditMode && uiState.exportDetailsSelected.localId == exportDetails.localId

                        AnimatedContent(
                            targetState = isEditing,
                            transitionSpec = {
                                (slideInVertically { it } + fadeIn()) togetherWith
                                        (slideOutVertically { -it } + fadeOut())
                            },
                            label = "Edit Switch"
                        ) { editing ->
                            if (editing) {

                                ExportDetailsCard(
                                    exportDetail = uiState.exportDetailsSelected,
                                    onIncrement = {
                                        viewModel.onAction(ExportDetailsState.Action.OnChangeQuantity(uiState.exportDetailsSelected.quantity + BigDecimal.ONE))
                                    },
                                    onDecrement = {
                                        viewModel.onAction(ExportDetailsState.Action.OnChangeQuantity(uiState.exportDetailsSelected.quantity - BigDecimal.ONE))
                                    },
                                    onUpdate = {
                                        viewModel.onAction(ExportDetailsState.Action.UpdateExportDetails)
                                        isEditMode = false
                                    },
                                    onClose = {
                                        isEditMode = false
                                    },
                                    isUpdating = true
                                )
                            } else {
                                SwipeableActionsBox(
                                    modifier = Modifier
                                        .padding(
                                            vertical = 8.dp,
                                        )
                                        .clip(RoundedCornerShape(16.dp)),
                                    startActions = listOf(
                                        SwipeAction(
                                            icon = rememberVectorPainter(Icons.Default.Edit),
                                            background = MaterialTheme.colorScheme.primary,
                                            onSwipe = {
                                                if (uiState.isEditable && !isCreating) {
                                                   viewModel.onAction(ExportDetailsState.Action.OnExportDetailsSelected(exportDetails))
                                                    isEditMode = true
                                                }else{
                                                    viewModel.onAction(ExportDetailsState.Action.NotifyCantEdit)

                                                }

                                            }
                                        )
                                    ),
                                    endActions = listOf(
                                        SwipeAction(
                                            icon = rememberVectorPainter(Icons.Default.Delete),
                                            background = MaterialTheme.colorScheme.error,
                                            onSwipe = {
                                                if (uiState.isEditable && !isCreating) {
                                                    viewModel.onAction(ExportDetailsState.Action.DeleteExportDetails(exportDetails.localId))
                                                }
                                                else{
                                                    viewModel.onAction(ExportDetailsState.Action.NotifyCantEdit)
                                                }

                                            }
                                        )
                                    )
                                ) {
                                    ExportDetailsCard(
                                        exportDetail = exportDetails
                                    )
                                }
                            }
                        }

                    }
                    item {
                        if (uiState.isEditable && !isCreating && !isEditMode) {
                            IconButton(
                                onClick = {
                                    isInventoryDialog = true
                                    isCreating = true
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add ExportDetails",
                                    modifier = Modifier.size(30.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }


            }
        }

        AnimatedVisibility(
            visible = uiState.isEditable  && !isEditMode,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
                LoadingButton(
                    onClick = {
                        if (uiState.isUpdating) viewModel.onAction(ExportDetailsState.Action.UpdateExport)
                        else viewModel.onAction(ExportDetailsState.Action.CreateExport)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
                    loading = false,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )

        }
    }
    if (isInventoryDialog) {
        Dialog(
            onDismissRequest = {
                isInventoryDialog = false
                isCreating = false
            },

            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceBright,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(30.dp)

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Nguyên liệu hiện có",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (inventories.itemCount == 0 && inventories.loadState.refresh !is LoadState.Loading) {
                        Nothing(
                            text = "Không có nguyên liệu nào",
                            icon = Icons.Default.Inventory2,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f)

                        ) {
                            gridItems(
                                inventories, 1, key = { inventory -> inventory.id },
                                itemContent = { inventory ->
                                    inventory?.let {
                                        InventoryPick(
                                            inventory = inventory,
                                            onClick = {
                                                viewModel.onAction(ExportDetailsState.Action.OnChangeInventory(inventory))
                                                isInventoryDialog = false
                                            }
                                        )
                                    }
                                },
                            )
                        }
                    }

                    Button(
                        onClick = {
                            isInventoryDialog = false
                            isCreating = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .heightIn(48.dp)
                            .align(Alignment.End),
                        shape = RoundedCornerShape(12.dp)


                    ) {
                        Text(text = "Đóng", modifier = Modifier.padding(horizontal = 16.dp))
                    }

                }
            }
        }
    }
}

@Composable
fun InventoryPick(
    inventory: Inventory,
    onClick: (Inventory) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.inversePrimary
            )
            .padding(8.dp)
            .clickable {
                onClick.invoke(inventory)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailsTextRow(
            text = inventory.ingredientName,
            icon = Icons.Default.Inventory,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DetailsTextRow(
                text = "HSD: ${StringUtils.formatLocalDate(inventory.expiryDate)}",
                icon = Icons.Default.DateRange,
                color = MaterialTheme.colorScheme.onSecondary
            )
            DetailsTextRow(
                text = "Số lượng: ${inventory.quantityRemaining} ${inventory.unit}",
                icon = Icons.Default.Tag,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }

}

@Composable
fun ExportDetailsCard(
    exportDetail: ExportDetailUIModel,
    isUpdating: Boolean = false,
    onIncrement: (() -> Unit)? = null,
    onDecrement: (() -> Unit)? = null,
    onUpdate: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isUpdating) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inversePrimary),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                if (!isUpdating) {
                    Icon(
                        imageVector = Icons.Default.Outbox,
                        contentDescription = "ExportDetails",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailsTextRow(
                        text = "Nguyên liệu: ${exportDetail.ingredientName}",
                        icon = Icons.Default.Inventory,
                        color = if (isUpdating) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSecondary
                    )
                    DetailsTextRow(
                        text = "HSD: ${StringUtils.formatLocalDate(exportDetail.expiryDate)}",
                        icon = Icons.Default.DateRange,
                        color = if (isUpdating) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSecondary
                    )

                    if (isUpdating) {
                        FoodItemCounter(
                            onCounterIncrement = { onIncrement?.invoke() },
                            onCounterDecrement = { onDecrement?.invoke() },
                            count = exportDetail.quantity.toInt(),
                            modifier = Modifier.align(Alignment.End)
                        )
                    } else {
                        DetailsTextRow(
                            text = "Số lượng: ${exportDetail.quantity}",
                            icon = Icons.Default.Tag,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }


            }

        }

        if (isUpdating) {
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { onClose?.invoke() },
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Confirm",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
                Button(
                    onClick = { onUpdate?.invoke() },
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.confirm),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = MaterialTheme.colorScheme.onConfirm,
                        modifier = Modifier
                            .size(32.dp)
                    )
                }
            }
        }
    }

}


package com.se114.foodapp.ui.screen.food_table

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTableScreen(
    navController: NavController,
    viewModel: FoodTableViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foodTables = viewModel.foodTables.collectAsLazyPagingItems()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showErrorSheet by remember { mutableStateOf(false) }
    var showFoodTableDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                FoodTableState.Event.OnBack -> {
                    navController.popBackStack()
                }

                FoodTableState.Event.ShowError -> {
                    showErrorSheet = true
                }

                is FoodTableState.Event.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                FoodTableState.Event.OnRefresh -> {
                    foodTables.refresh()
                }

            }
        }
    }

    Scaffold(
        floatingActionButton =
            {
                MyFloatingActionButton(
                    onClick = {
                        viewModel.onAction(FoodTableState.Action.OnFoodTableSelected(FoodTable()))
                        viewModel.onAction(FoodTableState.Action.OnUpdateStatus(false))
                        showFoodTableDialog = true
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
                onBack = {
                    viewModel.onAction(FoodTableState.Action.OnBack)
                },
                text = "Bàn ăn"
            )
            LazyPagingSample(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                items = foodTables,
                textNothing = "Không có bàn ăn nào",
                iconNothing = Icons.Default.TableRestaurant,
                columns = 2,
                key = {
                    it.id!!
                }
            ) {
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
                                viewModel.onAction(FoodTableState.Action.OnFoodTableSelected(it))
                                showDeleteDialog = true
                            }
                        ))
                ) {
                    FoodTableCard(
                        foodTable = it,
                        onClick = {
                            viewModel.onAction(FoodTableState.Action.OnFoodTableSelected(it))
                            viewModel.onAction(FoodTableState.Action.OnUpdateStatus(true))
                            showFoodTableDialog = true
                        }
                    )
                }
            }
        }
    }
    if (showFoodTableDialog) {
        Dialog(
            onDismissRequest = {
                showFoodTableDialog = false
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(30.dp)

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {


                    Text(
                        text = "Thông tin bàn ăn",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    FoodAppTextField(
                        labelText = "Số bàn",
                        value = uiState.foodTableSelected.tableNumber.toString(),
                        onValueChange = {
                            viewModel.onAction(FoodTableState.Action.OnChangeTableNumber(it.toIntOrNull()))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ChipsGroupWrap(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Số ghế",
                        options = listOf("1", "2", "3", "4", "5", "6"),
                        selectedOption = uiState.foodTableSelected.seatCapacity.toString(),
                        onOptionSelected = {
                            viewModel.onAction(FoodTableState.Action.OnChangeSeatCapacity(it.toInt()))
                        },

                        isFlowLayout = true,
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        Button(
                            onClick = {
                                showFoodTableDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.heightIn(48.dp),
                            shape = RoundedCornerShape(12.dp)


                        ) {
                            Text(text = "Đóng", modifier = Modifier.padding(horizontal = 16.dp))
                        }


                        LoadingButton(
                            onClick = {

                                if (uiState.isUpdating) {
                                    viewModel.onAction(FoodTableState.Action.OnUpdate)

                                } else viewModel.onAction(FoodTableState.Action.OnCreate)

                                showFoodTableDialog = false

                            },
                            text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
                            loading = uiState.isLoading,
                        )

                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        FoodAppDialog(
            title = "Xóa bàn ăn",
            message = "Bạn có chắc chắn muốn xóa bàn ăn này không?",
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                viewModel.onAction(FoodTableState.Action.OnDelete)
                showDeleteDialog = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error ?: "Lỗi không xác định",
            onDismiss = { showErrorSheet = false }
        )
    }
}


@Composable
fun FoodTableCard(
    foodTable: FoodTable,
    onClick: (FoodTable) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(162.dp)
            .height(147.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            )
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(
                onClick = { onClick.invoke(foodTable) },
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                imageVector = Icons.Default.TableRestaurant,
                contentDescription = "Food Table",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary),
            )
            Text(
                text = "Bàn ${foodTable.tableNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomStart)
            )

            Text(
                text = "Số chỗ: ${foodTable.seatCapacity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopEnd)
            )


        }
    }
}
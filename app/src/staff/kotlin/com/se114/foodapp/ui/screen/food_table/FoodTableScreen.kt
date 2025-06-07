package com.se114.foodapp.ui.screen.food_table

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.TabWithPager
import com.example.foodapp.ui.theme.confirm
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTableScreen(
    navController: NavController,
    viewModel: FoodTableViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foodTables = viewModel.getFoodTablesByTab(uiState.tabIndex).collectAsLazyPagingItems()
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
                    viewModel.onAction(FoodTableState.Action.OnRefresh)
                    foodTables.refresh()
                }

            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)

    ) {

        HeaderDefaultView(
            onBack = {
                viewModel.onAction(FoodTableState.Action.OnBack)
            },
            text = "Bàn ăn"
        )
        TabWithPager(
            modifier = Modifier.fillMaxWidth().weight(1f),
            tabs = listOf("Đang hoạt động", "Không hoạt động"),
            pages = listOf(
                {
                    FoodTableSection(
                        modifier = Modifier.fillMaxWidth(),
                        foodTables = foodTables,
                        onSwipe = {
                            viewModel.onAction(FoodTableState.Action.OnUpdateStatus(it, false))
                        },

                        isActive = true
                    )
                },
                {
                    FoodTableSection(
                        modifier = Modifier.fillMaxWidth(),
                        foodTables = foodTables,
                        onSwipe = {
                            viewModel.onAction(FoodTableState.Action.OnUpdateStatus(it, true))
                        },

                        isActive = false
                    )
                }
            ),

            onTabSelected = {
                viewModel.onAction(FoodTableState.Action.OnTabSelected(it))
            }
        )



        if (showErrorSheet) {
            ErrorModalBottomSheet(
                description = uiState.error ?: "Lỗi không xác định",
                onDismiss = { showErrorSheet = false }
            )
        }
    }
}
@Composable
fun FoodTableSection(
    modifier: Modifier = Modifier,
    foodTables: LazyPagingItems<FoodTable>,
    onSwipe: (Int)->Unit,

    isActive: Boolean

){
    LazyPagingSample(
        modifier = modifier,
        items = foodTables,
        textNothing = "Không có bàn ăn nào",
        iconNothing = Icons.Default.TableRestaurant,
        columns = 2,
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
                        icon = rememberVectorPainter(Icons.Default.FiberManualRecord),
                        background = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.confirm,
                        onSwipe = {
                            onSwipe.invoke(it.id!!.toInt())
                        }
                    ))
            ) {
                FoodTableCard(
                    foodTable = it,
                )
            }
        }
    )
}



@Composable
fun FoodTableCard(
    foodTable: FoodTable,
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
package com.se114.foodapp.ui.screen.menu


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.ui.navigation.AddMenuItem
import com.example.foodapp.ui.screen.components.DeleteBar
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal
import java.time.LocalTime

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MenuScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: MenuViewModel = hiltViewModel()
) {
    var search by remember { mutableStateOf("") }

    var isInSelectionMode by rememberSaveable { mutableStateOf(false) }
    var isSelectAll by rememberSaveable { mutableStateOf(false) }

    val sampleMenuItems = listOf(
        MenuItem(
            createdAt = LocalTime.of(11, 0),  // Giờ tạo item, ví dụ 11:00 AM
            description = "A freshly made Margherita pizza with mozzarella cheese, tomatoes, and basil.",
            id = 1L,
            menuName = "Pizza Menu",
            imageUrl = "https://example.com/images/margherita_pizza.jpg",
            name = "Margherita Pizza",
            price = BigDecimal("12.99")
        ),
        MenuItem(
            createdAt = LocalTime.of(12, 30),  // Giờ tạo item, ví dụ 12:30 PM
            description = "A juicy cheeseburger with fresh lettuce, tomato, and cheddar cheese.",
            id = 2L,
            menuName = "Burger Menu",
            imageUrl = "https://example.com/images/cheeseburger.jpg",
            name = "Cheeseburger",
            price = BigDecimal("8.99")
        ),
        MenuItem(
            createdAt = LocalTime.of(13, 15),  // Giờ tạo item, ví dụ 1:15 PM
            description = "Crispy fried chicken served with a side of mashed potatoes and gravy.",
            id = 3L,
            menuName = "Chicken Menu",
            imageUrl = "https://example.com/images/fried_chicken.jpg",
            name = "Fried Chicken",
            price = BigDecimal("10.49")
        ),
        MenuItem(
            createdAt = LocalTime.of(14, 0),  // Giờ tạo item, ví dụ 2:00 PM
            description = "A fresh salmon fillet grilled to perfection, served with steamed vegetables.",
            id = 4L,
            menuName =  "Seafood Menu",
            imageUrl = "https://example.com/images/grilled_salmon.jpg",
            name = "Grilled Salmon",
            price = BigDecimal("15.99")
        ),
        MenuItem(
            createdAt = LocalTime.of(16, 45),  // Giờ tạo item, ví dụ 4:45 PM
            description = "A creamy pasta dish with shrimp and a white wine garlic sauce.",
            id = 5L,
            menuName = "Pasta Menu",
            imageUrl = "https://example.com/images/shrimp_pasta.jpg",
            name = "Shrimp Pasta",
            price = BigDecimal("13.49")
        )
    )

    val showDialogDelete = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                MenuViewModel.MenuEvents.NavigateToDetail -> {

                }

                MenuViewModel.MenuEvents.ShowDeleteDialog -> {
                    showDialogDelete.value = true
                }
            }
        }
    }

    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(AddMenuItem)
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
                .padding(horizontal = 16.dp)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isInSelectionMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HeaderDefaultView(
                            text = "Danh sách món ăn"
                        )
                        SearchField(
                            searchInput = search,
                            searchChange = { search = it }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = isInSelectionMode,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    DeleteBar(
                        onSelectAll = {
                            isSelectAll = !isSelectAll
                            viewModel.selectAllItems(sampleMenuItems, isSelectAll)
                        },
                        onDeleteSelected = { viewModel.onRemoveClicked() }
                    )
                }
            }

//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth()
//
//            ) {
//                gridItems(foodItems, 2) { foodItem ->
//                    FoodItemView(
//                        foodItem = foodItem,
//                        animatedVisibilityScope = animatedVisibilityScope,
//                        isInSelectionMode = isInSelectionMode,
//                        isSelected = viewModel.selectedItems.contains(foodItem),
//                        onCheckedChange = { foodItem ->
//                            viewModel.toggleSelection(foodItem)
//                        },
//                        onClick = {
//                            navController.navigate(UpdateMenuItem(foodItem))
//                        },
//                        onLongClick = {
//                            isInSelectionMode = !isInSelectionMode
//                        },
//                        isCustomer = false
//                    )
//                }
//            }
        }
    }
    if (showDialogDelete.value) {

        FoodAppDialog(
            title = "Xóa món ăn",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn xóa món đã chọn khỏi danh sách không?",
            onDismiss = {

                showDialogDelete.value = false
            },
            onConfirm = {
                viewModel.removeItem()
                showDialogDelete.value = false
                isInSelectionMode = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
}


@Preview(showBackground = true)
@Composable
fun Previewmenu() {

    FoodAppTheme {

    }

}
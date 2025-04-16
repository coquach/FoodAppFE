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
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.navigation.AddMenuItem
import com.example.foodapp.ui.navigation.UpdateMenuItem
import com.example.foodapp.ui.screen.common.FoodItemView
import com.example.foodapp.ui.screen.components.DeleteBar
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal

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

    val foodItems = listOf(
        FoodItem(
            createdAt = "2025-03-29T10:00:00Z",
            description = "Burger bò phô mai thơm ngon với thịt bò tươi và phô mai tan chảy.",
            id = "1",
            imageUrl = "https://images.pexels.com/photos/1633578/pexels-photo-1633578.jpeg",
            name = "Burger Bò Phô Mai",
            price = BigDecimal("120000")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:05:00Z",
            description = "Pizza Margherita với lớp phô mai Mozzarella và sốt cà chua truyền thống.",
            id = "2",
            imageUrl = "https://images.pexels.com/photos/825661/pexels-photo-825661.jpeg",
            name = "Pizza Margherita",
            price =  BigDecimal("15000")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:10:00Z",
            description = "Gà rán giòn tan, vị cay nồng hấp dẫn, thích hợp cho bữa ăn nhanh.",
            id = "3",
            imageUrl = "https://images.pexels.com/photos/2271101/pexels-photo-2271101.jpeg",
            name = "Gà Rán Cay",
            price =  BigDecimal("14578")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:15:00Z",
            description = "Mì ramen Nhật Bản với nước dùng đậm đà và thịt heo Chashu mềm mại.",
            id = "4",
            imageUrl = "https://images.pexels.com/photos/2871752/pexels-photo-2871752.jpeg",
            name = "Mì Ramen",
            price =  BigDecimal("145678")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:20:00Z",
            description = "Sushi cá hồi tươi ngon, được làm từ cá hồi chất lượng cao.",
            id = "5",
            imageUrl = "https://images.pexels.com/photos/1099680/pexels-photo-1099680.jpeg",
            name = "Sushi Cá Hồi",
            price =  BigDecimal("1234567")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:25:00Z",
            description = "Bánh Pancake mềm mịn với sốt dâu tây và kem tươi.",
            id = "6",
            imageUrl = "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg",
            name = "Pancake Dâu",
            price =  BigDecimal("52144")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:30:00Z",
            description = "Ly cà phê cappuccino thơm béo với lớp bọt sữa mịn.",
            id = "7",
            imageUrl = "https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg",
            name = "Cappuccino",
            price =  BigDecimal("123456")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:35:00Z",
            description = "Sinh tố bơ thơm ngon, giàu chất dinh dưỡng.",
            id = "8",
            imageUrl = "https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg",
            name = "Sinh Tố Bơ",
            price =  BigDecimal("245334")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:40:00Z",
            description = "Kem dâu tây tươi mát, tan ngay trong miệng.",
            id = "9",
            imageUrl = "https://images.pexels.com/photos/1028429/pexels-photo-1028429.jpeg",
            name = "Kem Dâu",
            price =  BigDecimal("78942")
        ),
        FoodItem(
            createdAt = "2025-03-29T10:45:00Z",
            description = "Ly nước chanh mát lạnh giúp giải nhiệt mùa hè.",
            id = "10",
            imageUrl = "https://images.pexels.com/photos/1763075/pexels-photo-1763075.jpeg",
            name = "Nước Chanh",
            price =  BigDecimal("124635")
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
                            viewModel.selectAllItems(foodItems, isSelectAll)
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
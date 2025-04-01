package com.se114.foodapp.ui.screen.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.FoodAppTextField

import com.example.foodapp.ui.ItemCount
import com.example.foodapp.ui.MyFloatingActionButton
import com.example.foodapp.ui.gridItems
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.screen.common.FoodItemView
import com.se114.foodapp.ui.screen.home.categories.CategoriesList
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.theme.FoodAppTheme
import com.se114.foodapp.ui.screen.home.banner.Banners
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel

) {
    val unReadCount by notificationViewModel.unreadCount.collectAsState()
    val cartSize by viewModel.cartSize.collectAsState()

    val searchInput = remember { mutableStateOf("") }
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            when (it) {
                HomeViewModel.HomeNavigationEvents.NavigateToDetail -> {

                }

                HomeViewModel.HomeNavigationEvents.NavigateToNotification -> {
                    navController.navigate(Notification)
                }

            }
        }
    }
    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(Cart)
                },
                bgColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Box(modifier = Modifier.size(56.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cart),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier.align(Center)
                    )

                    if (cartSize > 0) {
                        ItemCount(cartSize)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
                .padding(16.dp)

        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(50.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nofication),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .align(Center)
                                .clickable {
                                    viewModel.onNotificationClicked()
                                }
                        )

                        if (unReadCount > 0) {
                            ItemCount(unReadCount)
                        }

                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                SearchField(searchInput)
                Spacer(modifier = Modifier.size(8.dp))
                Banners()
            }

//            val uiSate = viewModel.uiState.collectAsStateWithLifecycle()
//            when (uiSate.value) {
//                is HomeViewModel.HomeState.Loading -> {
//                    CircularProgressIndicator()
//                }
//
//                is HomeViewModel.HomeState.Empty -> {
//                    // Show empty state message
//                }
//
//                is HomeViewModel.HomeState.Success -> {
//                    val categories = viewModel.categories
//                    CategoriesList(categories = categories, onCategorySelected = {
//
//                    })
//                }

            val foodItems = listOf(
                FoodItem(
                    createdAt = "2025-03-29T10:00:00Z",
                    description = "Burger bò phô mai thơm ngon với thịt bò tươi và phô mai tan chảy.",
                    id = "1",
                    imageUrl = "https://images.pexels.com/photos/1633578/pexels-photo-1633578.jpeg",
                    name = "Burger Bò Phô Mai",
                    price = 146755f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:05:00Z",
                    description = "Pizza Margherita với lớp phô mai Mozzarella và sốt cà chua truyền thống.",
                    id = "2",
                    imageUrl = "https://images.pexels.com/photos/825661/pexels-photo-825661.jpeg",
                    name = "Pizza Margherita",
                    price = 207705f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:10:00Z",
                    description = "Gà rán giòn tan, vị cay nồng hấp dẫn, thích hợp cho bữa ăn nhanh.",
                    id = "3",
                    imageUrl = "https://images.pexels.com/photos/2271101/pexels-photo-2271101.jpeg",
                    name = "Gà Rán Cay",
                    price = 171255f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:15:00Z",
                    description = "Mì ramen Nhật Bản với nước dùng đậm đà và thịt heo Chashu mềm mại.",
                    id = "4",
                    imageUrl = "https://images.pexels.com/photos/2871752/pexels-photo-2871752.jpeg",
                    name = "Mì Ramen",
                    price = 195655f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:20:00Z",
                    description = "Sushi cá hồi tươi ngon, được làm từ cá hồi chất lượng cao.",
                    id = "5",
                    imageUrl = "https://images.pexels.com/photos/1099680/pexels-photo-1099680.jpeg",
                    name = "Sushi Cá Hồi",
                    price = 244755f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:25:00Z",
                    description = "Bánh Pancake mềm mịn với sốt dâu tây và kem tươi.",
                    id = "6",
                    imageUrl = "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg",
                    name = "Pancake Dâu",
                    price = 122255f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:30:00Z",
                    description = "Ly cà phê cappuccino thơm béo với lớp bọt sữa mịn.",
                    id = "7",
                    imageUrl = "https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg",
                    name = "Cappuccino",
                    price = 97755f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:35:00Z",
                    description = "Sinh tố bơ thơm ngon, giàu chất dinh dưỡng.",
                    id = "8",
                    imageUrl = "https://images.pexels.com/photos/140831/pexels-photo-140831.jpeg",
                    name = "Sinh Tố Bơ",
                    price = 110025f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:40:00Z",
                    description = "Kem dâu tây tươi mát, tan ngay trong miệng.",
                    id = "9",
                    imageUrl = "https://images.pexels.com/photos/1028429/pexels-photo-1028429.jpeg",
                    name = "Kem Dâu",
                    price = 73255f
                ),
                FoodItem(
                    createdAt = "2025-03-29T10:45:00Z",
                    description = "Ly nước chanh mát lạnh giúp giải nhiệt mùa hè.",
                    id = "10",
                    imageUrl = "https://images.pexels.com/photos/1763075/pexels-photo-1763075.jpeg",
                    name = "Nước Chanh",
                    price = 61155f
                )
            )



            item {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                ) {
                    gridItems(foodItems, 2) { foodItem ->
                        FoodItemView(
                            foodItem = foodItem,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onClick = {
                                navController.navigate(FoodDetails(foodItem))
                            })
                    }
                }
            }


//            }


        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    searchInput: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            FoodAppTextField(
                value = searchInput.value,
                onValueChange = { it },
                placeholder = {
                    Text(text = "Tìm kiếm ở đây", color = MaterialTheme.colorScheme.outline)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        tint = MaterialTheme.colorScheme.outline,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(50.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }


    }
}






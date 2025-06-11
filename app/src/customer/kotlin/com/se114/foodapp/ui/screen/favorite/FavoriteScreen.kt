package com.se114.foodapp.ui.screen.favorite

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Food
import com.example.foodapp.navigation.FoodDetails
import com.example.foodapp.ui.screen.common.FoodList
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.ui.screen.components.TabWithPager
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FavoriteScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FavoriteViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foods by viewModel.foodsTabManager.tabDataMap.collectAsStateWithLifecycle()
    val foodFavorite = viewModel.favoriteFoods.collectAsLazyPagingItems()
    val emptyFoods = MutableStateFlow<PagingData<Food>>(PagingData.empty()).collectAsLazyPagingItems()
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when(it){
                is FavoriteState.Event.NavigateToDetail -> {
                    navController.navigate(FoodDetails(it.food))
                }
                FavoriteState.Event.ShowError -> {

                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Box(
            modifier = Modifier.fillMaxWidth().padding(top= 20.dp)
        ){

            SearchField(
                searchInput = uiState.nameSearch,
                searchChange = {
                    viewModel.onAction(FavoriteState.Action.OnChangeNameSearch(it))
                },
                searchPlaceholder = "Nhập tên món ăn...",
            )
        }
        TabWithPager(
            tabs = listOf("Tất cả", "Yêu thích"),
            pages = listOf(
                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ChipsGroupWrap(
                            modifier = Modifier.fillMaxWidth(),
                            options = uiState.menus.map { it.name },
                            selectedOption = uiState.menuName,
                            onOptionSelected = { selectedName ->
                                val selectedMenu = uiState.menus.find { it.name == selectedName }
                                selectedMenu?.let {
                                    viewModel.onAction(FavoriteState.Action.OnMenuClicked(it.id!!, it.name))
                                    viewModel.getFoodsFlow(it.id)
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.outline,
                            isFlowLayout = false,
                            shouldSelectDefaultOption = true
                        )
                        FoodList(
                            foods = foods[uiState.foodFilter.menuId!!]?.flow?.collectAsLazyPagingItems() ?: emptyFoods,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {
                                viewModel.onAction(FavoriteState.Action.OnFoodClick(it))
                            },
                            isCustomer = true,
                            isFullWidth = true,
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )

                    }


                },
                {
                    FoodList(
                        modifier = Modifier.fillMaxSize(),
                        foods = foodFavorite,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onItemClick = {
                            viewModel.onAction(FavoriteState.Action.OnFoodClick(it))
                        },
                        isCustomer = true,
                        isFullWidth = true,
                        isAnimated = false
                    )
                }
            ),
            onTabSelected = {

            },
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
    }

}
package com.se114.foodapp.ui.screen.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.navigation.Cart
import com.example.foodapp.ui.screen.common.FoodList
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.ItemCount
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.SearchField
import com.example.foodapp.utils.StringUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeStaffScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeStaffViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foods = viewModel.getFoodsByMenuId(uiState.menuIdSelected).collectAsLazyPagingItems()
    val menus = viewModel.menus.collectAsLazyPagingItems()

    var isOpenFoodDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    var search by remember { mutableStateOf("") } //demo

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event ->
                when (event) {
                    HomeStaffState.Event.GoToCart -> {
                        navController.navigate(Cart)
                    }


                    HomeStaffState.Event.OnAddToCart -> {
                        Toast.makeText(context, "Đã thêm món trong giỏ hàng", Toast.LENGTH_SHORT)
                            .show()
                    }

                    HomeStaffState.Event.OnItemAlreadyInCart -> {
                        Toast.makeText(
                            context,
                            "Đã câp nhật món trong giỏ hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    HomeStaffState.Event.ShowError -> {
                        showErrorSheet = true
                    }

                }
            }
    }
    Scaffold(
        floatingActionButton =
            {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    MyFloatingActionButton(
                        onClick = {
                            viewModel.onAction(HomeStaffState.Action.OnCartClicked)
                        },
                        bgColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Box(modifier = Modifier.size(56.dp)) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Center)
                                    .size(24.dp)
                            )

                            if (uiState.cartSize > 0) {
                                ItemCount(uiState.cartSize)
                            }
                        }
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


            SearchField(
                searchInput = search,
                searchChange = { search = it }
            )





            FoodList(
                foods = foods,
                animatedVisibilityScope = animatedVisibilityScope,
                onItemClick = {
                    viewModel.onAction(HomeStaffState.Action.OnFoodClicked(it))
                },
                isCustomer = false,
            )


        }


    }
    if (isOpenFoodDialog) {
        FoodHomeStaffDialog(
            onDismiss = {
                isOpenFoodDialog = false
            },
            food = uiState.foodSelected!!,
            animatedVisibilityScope = animatedVisibilityScope,
            onConfirm = {
                viewModel.onAction(HomeStaffState.Action.OnAddToCart)
                isOpenFoodDialog = false
            },
            isLoading = uiState.isLoading,
            onIncreaseQuantity = {
                viewModel.onAction(HomeStaffState.Action.OnChangeQuantity(uiState.foodSelected!!.quantity + 1))
            },
            onDecreaseQuantity = {
                viewModel.onAction(HomeStaffState.Action.OnChangeQuantity(uiState.foodSelected!!.quantity - 1))
            }
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodHomeStaffDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    isLoading: Boolean,
    food: FoodUiHomeStaffModel,
    animatedVisibilityScope: AnimatedVisibilityScope
){
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = modifier
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    AsyncImage(
                        model = food.image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(82.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.sharedElement(
                            state = rememberSharedContentState(key = "title/${food.id}"),
                            animatedVisibilityScope
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${food.totalRating}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)

                    )

                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Liked",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Red
                    )

                    Text(
                        text = "${food.totalLike}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)

                    )

                    Icon(
                        imageVector = Icons.Filled.Feedback,
                        contentDescription = "Feedback",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "(${food.totalFeedback}+)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )


                }

                HorizontalDivider(
                    thickness = 0.3.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .sharedElement(
                            state = rememberSharedContentState(key = "description/${food.id}"),
                            animatedVisibilityScope
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Text(
                        text = StringUtils.formatCurrency(food.price),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    FoodItemCounter(
                        count = food.quantity,
                        onCounterIncrement = onIncreaseQuantity,
                        onCounterDecrement = onDecreaseQuantity
                    )

                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.outline
                        ),
                        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline),
                        modifier = Modifier.heightIn(48.dp),
                        shape = RoundedCornerShape(12.dp)


                    ) {
                        Text(text = "Đóng", modifier = Modifier.padding(horizontal = 16.dp))
                    }


                    LoadingButton(
                        onClick = onConfirm,
                        loading = isLoading,
                        text = "Thêm"
                    )
                }


            }
        }
    }
}
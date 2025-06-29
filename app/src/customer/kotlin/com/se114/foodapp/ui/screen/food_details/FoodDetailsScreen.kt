package com.se114.foodapp.ui.screen.food_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.navigation.Cart
import com.example.foodapp.ui.screen.components.AppButton
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.ui.component.ImageCarousel
import com.se114.foodapp.ui.screen.feedback.FeedbackList
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val feedbacks = viewModel.getFeedbacks().collectAsLazyPagingItems()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    var successMessage by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect { event ->
            when (event) {
                FoodDetails.Event.GoToCart -> {
                    navController.navigate(Cart)
                }

                FoodDetails.Event.OnAddToCart -> {
                    successMessage = "Đã thêm món trong giỏ hàng"
                    showSuccessDialog = true
                }

                FoodDetails.Event.OnBack -> {
                    navController.popBackStack()
                }

                FoodDetails.Event.OnItemAlreadyInCart -> {
                    successMessage = "Món đã có trong giỏ hàng"
                    showSuccessDialog = true
                }

                FoodDetails.Event.ShowError -> {
                    showErrorSheet = true
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
        val images = uiState.food.images?.map {
            it.url
        }
        MenuHeader(
            images = images ?: emptyList(),
            foodId = uiState.food.id,
            onBackButton = {
                navController.popBackStack()
            },
            onFavoriteButton = {
                viewModel.onAction(FoodDetails.Action.OnFavorite(uiState.food.id))
            },

            animatedVisibilityScope = animatedVisibilityScope,
            isFavorite = uiState.food.liked
        )
        FoodDetail(
            modifier = Modifier.fillMaxWidth(),
            title = uiState.food.name,
            description = uiState.food.description,
            foodId = uiState.food.id,
            animatedVisibilityScope = animatedVisibilityScope,
            totalRating = uiState.food.totalRating,
            totalFeedbacks = uiState.food.totalFeedback,
            totalLikes = uiState.food.totalLikes,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(
                text = StringUtils.formatCurrency(uiState.food.price),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            FoodItemCounter(
                count = uiState.quantity,
                onCounterIncrement = {
                    viewModel.onAction(FoodDetails.Action.OnChangeQuantity(uiState.quantity + 1))
                },
                onCounterDecrement = {
                    viewModel.onAction(FoodDetails.Action.OnChangeQuantity(uiState.quantity - 1))
                }
            )

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Đánh giá",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            FeedbackList(
                feedbacks = feedbacks,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
        LoadingButton(
            onClick = {
                viewModel.onAction(FoodDetails.Action.OnAddToCart)
            },
            modifier = Modifier.fillMaxWidth(),
            text = "Thêm vào giỏ hàng",
            loading = uiState.isLoading,
            enabled = !uiState.isLoading,

        )
    }

    if (showSuccessDialog) {
        ModalBottomSheet(
            onDismissRequest = { showSuccessDialog = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = successMessage,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                AppButton(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.onAction(FoodDetails.Action.GoToCart)
                    }, modifier = Modifier

                        .fillMaxWidth(),
                    text = "Xem giỏ hàng"
                )

                AppButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showSuccessDialog = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Tiếp tục mua hàng"

                )

            }
        }
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = { showErrorSheet = false },

            )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetail(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    foodId: Long,
    totalRating: Double,
    totalFeedbacks: Int,
    totalLikes: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.sharedElement(
                state = rememberSharedContentState(key = "title/${foodId}"),
                animatedVisibilityScope
            ),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "$totalRating",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterVertically)

            )

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Liked",
                modifier = Modifier.size(30.dp),
                tint = Color.Red
            )

            Text(
                text = "$totalLikes",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterVertically)

            )

            Icon(
                imageVector = Icons.Filled.Feedback,
                contentDescription = "Feedback",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = "($totalFeedbacks+)",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )


        }

        HorizontalDivider(
            thickness = 0.3.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(top = 8.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "description/${foodId}"),
                    animatedVisibilityScope
                )
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MenuHeader(
    images: List<String>,
    foodId: Long,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackButton: () -> Unit,
    onFavoriteButton: () -> Unit,
    isFavorite: Boolean,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = {
                onBackButton.invoke()
            },
            modifier = Modifier

                .padding(16.dp)
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.onPrimary)
                .padding(4.dp)
                .zIndex(2f),

            ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(30.dp)
            )
        }


        IconButton(
            onClick = onFavoriteButton,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                .align(Alignment.TopEnd)
                .zIndex(2f)

        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
        ImageCarousel(
            modifier = Modifier.zIndex(1f),
            images = images,
            animatedVisibilityScope = animatedVisibilityScope,
            foodId = foodId
        )


    }
}




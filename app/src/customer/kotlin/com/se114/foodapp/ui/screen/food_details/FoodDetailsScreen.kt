package com.se114.foodapp.ui.screen.food_details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.navigation.Cart
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodItemCounter
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
    val showSuccessDialog = remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    val successMessage = remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect { event ->
            when (event) {
                FoodDetails.Event.GoToCart -> {
                    navController.navigate(Cart)
                }

                FoodDetails.Event.OnAddToCart -> {
                    successMessage.value = "Đã thêm món trong giỏ hàng"
                    showSuccessDialog.value = true
                }

                FoodDetails.Event.OnBack -> {
                    navController.popBackStack()
                }

                FoodDetails.Event.OnItemAlreadyInCart -> {
                    successMessage.value = "Món đã có trong giỏ hàng"
                    showSuccessDialog.value = true
                }

                FoodDetails.Event.ShowError -> {
                    showErrorSheet = true
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
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
                .padding(16.dp)

        ) {
            Text(
                text = StringUtils.formatCurrency(uiState.food.price),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge
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
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Đánh giá",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            FeedbackList(
                feedbacks = feedbacks
            )
        }
        Button(
            onClick = {
                viewModel.onAction(FoodDetails.Action.OnAddToCart)
            },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box {
                AnimatedContent(
                    targetState = uiState.isLoading,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                    }
                ) { target ->
                    if (target) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .size(24.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Thêm vào giỏ hàng".uppercase(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

        }
    }

    if (showSuccessDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showSuccessDialog.value = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = successMessage.value,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        showSuccessDialog.value = false
                        viewModel.onAction(FoodDetails.Action.GoToCart)
                    }, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Đi đến giỏ hàng")
                }

                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showSuccessDialog.value = false
                        }
                    }, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Trở lại")
                }

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
            )
        )

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
                text = "$totalRating",
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
                text = "$totalLikes",
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
                text = "($totalFeedbacks+)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )


        }

        HorizontalDivider(
            thickness = 0.3.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
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
        ImageCarousel(
            images = images,
            animatedVisibilityScope = animatedVisibilityScope,
            foodId = foodId
        )

        IconButton(
            onClick = {
                onBackButton.invoke()
            },
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.onPrimary)
                .padding(4.dp),


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
            onClick = { onFavoriteButton },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                .align(Alignment.TopEnd)

        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favorite",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}




package com.example.foodapp.ui.screen.common


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.R.drawable
import com.example.foodapp.data.model.Food
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.utils.StringUtils
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.FoodView(
    food: Food,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (Food) -> Unit,
    isCustomer: Boolean = true,
    isAnimated: Boolean = true,
    isFullWidth: Boolean = false,
) {

    Column(
        modifier = Modifier
            .padding(8.dp)
            .then(
                if (isFullWidth) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.width(162.dp)
                }
            )
            .height(216.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
            )
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable(
                onClick = { onClick.invoke(food) },
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(147.dp)
        ) {
            AsyncImage(
                model = food.images?.firstOrNull()?.url, contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isAnimated) {
                            Modifier.sharedElement(
                                state = rememberSharedContentState(key = "image/${food.id}"),
                                animatedVisibilityScope
                            )
                        } else {
                            Modifier // 👈 Modifier.empty, tức là không gắn gì cả
                        }
                    ),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(drawable.ic_placeholder),
                error = painterResource(drawable.ic_placeholder)
            )
            Text(
                text = StringUtils.formatCurrency(food.price),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopStart)
            )
            if (isCustomer) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color = if (food.liked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = MaterialTheme.colorScheme.outlineVariant)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${food.totalRating}",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Yellow
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "(${food.totalFeedback})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }







        }

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier
                    .then(
                        if (isAnimated) {
                            Modifier.sharedElement(
                    state = rememberSharedContentState(key = "title/${food.id}"),
                    animatedVisibilityScope
                )} else{
                            Modifier
                }),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = food.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .then(
                        if (isAnimated) {
                            Modifier.sharedElement(
                                state = rememberSharedContentState(key = "description/${food.id}"),
                                animatedVisibilityScope
                            )} else{
                            Modifier
                        })
            )
        }
    }


}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodList(
    modifier: Modifier = Modifier,
    foods: LazyPagingItems<Food>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (Food) -> Unit,
    isCustomer: Boolean = false,
    isSwipeAction: Boolean = false,
    isFullWidth: Boolean = false,
    isAnimated: Boolean = true,
    endAction: (@Composable (Food) -> SwipeAction)? = null,
    onRefresh: () -> Unit = {}

) {
    LazyPagingSample(
        modifier = modifier,
        items = foods,
        textNothing = "Không có món ăn nào",
        iconNothing = Icons.Default.NoMeals,
        columns = if (isFullWidth) 1 else 2,
        key = {food ->
            food.id
        },
        onRefresh = onRefresh
    ) {food ->
        if (isSwipeAction) {

            SwipeableActionsBox(
                modifier = Modifier
                    .padding(
                        8.dp,
                    )
                    .clip(RoundedCornerShape(12.dp)),
                endActions = endAction?.let { listOf(it(food)) } ?: emptyList()
            ) {
                FoodView(
                    food = food,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onClick = { onItemClick(food) },
                    isCustomer = isCustomer,
                    isAnimated = isAnimated,
                    isFullWidth = isFullWidth
                )
            }


        } else {
            FoodView(
                food = food,
                animatedVisibilityScope = animatedVisibilityScope,
                onClick = { onItemClick(food) },
                isCustomer = isCustomer,
                isAnimated = isAnimated,
                isFullWidth = isFullWidth
            )
        }
    }


}
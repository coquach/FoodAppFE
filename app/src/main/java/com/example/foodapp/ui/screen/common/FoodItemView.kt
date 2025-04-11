package com.example.foodapp.ui.screen.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.screen.components.CustomCheckbox
import com.example.foodapp.utils.StringUtils

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.FoodItemView(
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (FoodItem) -> Unit,
    onLongClick: ((Boolean) -> Unit)? = null,
    isInSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onCheckedChange: ((FoodItem) -> Unit)? = null,
    isCustomer: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AnimatedVisibility(visible = isInSelectionMode) {
        CustomCheckbox(
            checked = isSelected,
            onCheckedChange = { onCheckedChange?.invoke(foodItem) },
        )
    }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(162.dp)
                .height(216.dp)
                .graphicsLayer {
                    alpha = if (isSelected && isInSelectionMode ) 0.7f else 1f
                }
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                    spotColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)
                )
                .background(color = MaterialTheme.colorScheme.surface)
                .combinedClickable(
                    onClick = { onClick.invoke(foodItem) },
                    onLongClick = {
                        onLongClick?.invoke(true)
                    }
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(147.dp)
            ) {
                AsyncImage(
                    model = foodItem.imageUrl, contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .sharedElement(
                            state = rememberSharedContentState(key = "image/${foodItem.id}"),
                            animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = StringUtils.formatCurrency(foodItem.price),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.outlineVariant)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.TopStart)
                )
//            Box(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .align(Alignment.TopEnd)
//                    .size(30.dp)
//                    .clip(CircleShape)
//                    .background(color = MaterialTheme.colorScheme.primary)
//                    .padding(4.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Favorite,
//                    contentDescription = null,
//                    modifier = Modifier.size(25.dp),
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//            }




                if (isCustomer){
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
                            text = "4.5", style = MaterialTheme.typography.titleSmall, maxLines = 1
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
                            text = "(21)",
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
                    text = foodItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = "title/${foodItem.id}"),
                        animatedVisibilityScope
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = foodItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}
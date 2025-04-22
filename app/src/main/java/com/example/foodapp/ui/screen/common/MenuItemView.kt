package com.example.foodapp.ui.screen.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Order
import com.example.foodapp.ui.screen.components.CustomCheckbox
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.gridItems
import com.example.foodapp.utils.StringUtils

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun SharedTransitionScope.MenuItemView(
    menuItem: MenuItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (MenuItem) -> Unit,
    onLongClick: ((Boolean) -> Unit)? = null,
    isInSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onCheckedChange: ((MenuItem) -> Unit)? = null,
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
            onCheckedChange = { onCheckedChange?.invoke(menuItem) },
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
                    onClick = { onClick.invoke(menuItem) },
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
                    model = menuItem.imageUrl, contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .sharedElement(
                            state = rememberSharedContentState(key = "image/${menuItem.id}"),
                            animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = StringUtils.formatCurrency(menuItem.price),
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
                    text = menuItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = "title/${menuItem.id}"),
                        animatedVisibilityScope
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MenuItemList(
    menuItems: LazyPagingItems<MenuItem>,
    isInSelectionMode :Boolean,
    isSelected: (MenuItem) -> Boolean,
    onCheckedChange: (MenuItem) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (MenuItem) -> Unit,
    onLongClick: ((Boolean) -> Unit)? = null,
    isCustomer: Boolean = false
) {
    if (menuItems.itemSnapshotList.items.isEmpty() && menuItems.loadState.refresh !is LoadState.Loading ) {
        Nothing(
            text = "Không có món ăn nào",
            icon = Icons.Default.NoMeals
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            gridItems(menuItems, 2, key = { menuItem -> menuItem.id }) { menuItem ->
                menuItem?.let {
                    MenuItemView(
                        menuItem = menuItem,
                        animatedVisibilityScope = animatedVisibilityScope,
                        isInSelectionMode = isInSelectionMode,
                        isSelected = isSelected(menuItem),
                        onCheckedChange = onCheckedChange,
                        onClick = { onItemClick(menuItem) },
                        onLongClick = onLongClick,
                        isCustomer = isCustomer
                    )
                }
            }
        }
    }
}
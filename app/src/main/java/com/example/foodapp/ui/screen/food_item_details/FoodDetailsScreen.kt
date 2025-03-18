package com.example.foodapp.ui.screen.food_item_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Transition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.models.response.FoodItem
import com.example.foodapp.ui.theme.FoodAppTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val count = rememberSaveable { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        FoodHeader(
            imageUrl = foodItem.imageUrl,
            foodID = foodItem.id ?: "default id",
            onBackButton = {
                navController.popBackStack()
            },
            onFavoriteButton = {},
            animatedVisibilityScope = animatedVisibilityScope
        )
        FoodDetail(
            title = foodItem.name,
            description = foodItem.description,
            foodID = foodItem.id?: "default id",
            animatedVisibilityScope = animatedVisibilityScope
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "$${foodItem.price}", color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.size(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.minus),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {

            }) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(32.dp)),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = !isLoading.value) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.cart),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Add to Cart".uppercase(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }
                AnimatedVisibility(visible = isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }

        }
    }
}



@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetail(
    title: String,
    description: String,
    foodID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.sharedElement(
                state = rememberSharedContentState(key = "title/${foodID}"),
                animatedVisibilityScope
            )
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "4.5",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "(30+)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { /*TODO*/ }) {
                Text(
                    text = " View All Reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodHeader(
    imageUrl: String,
    foodID: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackButton: () -> Unit,
    onFavoriteButton: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageUrl, contentDescription = null, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "image/${foodID}"),
                    animatedVisibilityScope
                )
                .clip(
                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                ), contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onBackButton,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.TopStart)
        ) {
            Image(painter = painterResource(id = R.drawable.back), contentDescription = null)
        }
        IconButton(
            onClick = onFavoriteButton,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.favorite), contentDescription = null
            )
        }
    }
}




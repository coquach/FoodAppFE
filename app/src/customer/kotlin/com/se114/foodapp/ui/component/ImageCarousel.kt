package com.se114.foodapp.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.CustomPagerIndicator

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ImageCarousel(
    images: List<String>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodId: Long,
) {
    if (images.isEmpty()) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = "empty",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        return
    }
    val pagerState = rememberPagerState(initialPage = 0) {
        images.size
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        ) { index ->
            CardContent(images[index], index, pagerState, animatedVisibilityScope, foodId)
        }
        if (images.size > 1) { // Only show indicator if there's more than one image
            CustomPagerIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp), // Adjusted padding
                pageCount = images.size,
                currentPage = pagerState.currentPage
            )
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CardContent(
    image: String,
    index: Int,
    paperState: PagerState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodId: Long,
) {
    val pageOffset = (paperState.currentPage - index) + paperState.currentPageOffsetFraction
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .height(200.dp)

            .graphicsLayer {
                val scale = androidx.compose.ui.util.lerp(
                    start = 0.85f, // Start scale for pages further away
                    stop = 1f,     // End scale for the page in center
                    fraction = 1f - pageOffset.coerceIn(0f, 1f) // 0f when far, 1f at center
                )
                scaleX = scale
                scaleY = scale
            }
    ) {
        if (index == 0) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        state = rememberSharedContentState(key = "title/${foodId}"),
                        animatedVisibilityScope
                    ),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .scale(Scale.FIT)
                    .build(),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
            )
        } else {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .scale(Scale.FIT)
                    .build(),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
            )
        }


    }
}


package com.se114.foodapp.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.CustomPagerIndicator
import kotlin.math.absoluteValue


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ImageCarousel(
    modifier: Modifier= Modifier,
    images: List<String>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodId: Long,
) {

    val pagerState = rememberPagerState(initialPage = 0) {
        images.size
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 10.dp),
        ) { index ->
            CardContent(images[index], index, pagerState,animatedVisibilityScope, foodId)
        }
        if (images.size > 1) { // Only show indicator if there's more than one image
            CustomPagerIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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
            .padding(3.dp)
            .graphicsLayer {
                lerp(
                    start = 0.65f.dp,
                    stop =  1f.dp,
                    fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 0.2f)
                ).also { scale ->
                    scaleX = scale.value
                    scaleY = scale.value
                }
                alpha = lerp(
                    start = 0.5f.dp,
                    stop =  1f.dp,
                    fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                ).value
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





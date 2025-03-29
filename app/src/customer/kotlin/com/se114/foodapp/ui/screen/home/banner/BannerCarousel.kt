package com.se114.foodapp.ui.screen.home.banner

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.example.foodapp.ui.CustomPagerIndicator
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun Banners(
) {
    val pages = listOf(
        Slider.First,
        Slider.Second,
        Slider.Third
    )
    AutoSlidingCarousel(pages = pages)
}

@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier,
    pages: List<Slider>
) {

    val pagerState = rememberPagerState(pageCount = { pages.size }, initialPage = 0)
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()

    val pageInteractionSource = remember { MutableInteractionSource() }
    val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

    // Stop auto-advancing when pager is dragged or one of the pages is pressed
    val autoAdvance = !pagerIsDragged && !pageIsPressed

    if (autoAdvance) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(5000)
                val nextPage = (pagerState.currentPage + 1) % pages.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 8.dp,
        ) { position ->
            val pageOffset = ((pagerState.currentPage - position) + pagerState.currentPageOffsetFraction).absoluteValue

            val height by animateDpAsState(
                targetValue = lerp(150.dp, 180.dp, 1 - pageOffset.coerceIn(0f, 1f)),
                animationSpec = tween(300)
            )

            val scale by animateFloatAsState(
                targetValue = lerp(1f, 1.2f, 1 - pageOffset.coerceIn(0f, 1f)),
                animationSpec = tween(300)
            )


            Image(
                    painter = painterResource(id = pages[position].image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .clip(RoundedCornerShape(30.dp))
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                        .padding(8.dp),
                    contentScale = ContentScale.Crop,
                )
        }

        CustomPagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            pageCount = pages.size,
            currentPage = pagerState.currentPage
        )
    }


}



@Preview
@Composable
fun PreviewBannerScreen() {
    val pages = listOf(
        Slider.First,
        Slider.Second,
        Slider.Third
    )
    AutoSlidingCarousel(pages = pages)
}
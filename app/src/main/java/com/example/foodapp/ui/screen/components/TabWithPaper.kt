package com.example.foodapp.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.foodapp.data.model.Order
import kotlinx.coroutines.launch

@Composable
fun TabWithPager(
    tabs: List<String>,
    pages: List<@Composable () -> Unit>
) {
    require(tabs.size == pages.size) { "Số lượng tabs và pages phải bằng nhau" }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 0)

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier

                .clip(RoundedCornerShape(32.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(4.dp),
            indicator = {},
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            pages[page].invoke()
        }
    }
}

@Composable
fun <T> GenericListContent(
    list: List<T>,
    iconEmpty: ImageVector,
    columns: Int = 1,
    textEmpty: String,
    itemContent: @Composable (T) -> Unit
) {
    if (list.isEmpty()) {
        Nothing(
            icon = iconEmpty,
            text = textEmpty
        )
    } else {

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            items(list) { item ->
                itemContent(item)
            }
        }

    }
}
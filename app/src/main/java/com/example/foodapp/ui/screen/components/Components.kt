package com.example.foodapp.ui.screen.components


import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Animatable

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import co.yml.charts.common.extensions.isNotNull

import com.example.foodapp.R
import com.example.foodapp.data.model.Order
import com.example.foodapp.ui.screen.common.OrderItemView
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun FoodAppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    labelText: String? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(10.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors().copy(
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        errorPlaceholderColor = MaterialTheme.colorScheme.error,
        errorIndicatorColor = MaterialTheme.colorScheme.error,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    ),
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        labelText?.let {

            Text(
                text = it,
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )


        }


        OutlinedTextField(
            value = value,
            onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled,
            readOnly,
            textStyle,
            null,
            placeholder,
            leadingIcon,
            trailingIcon,
            prefix,
            suffix,
            supportingText,
            isError,
            visualTransformation,
            keyboardOptions,
            keyboardActions,
            singleLine,
            maxLines,
            minLines,
            interactionSource,
            shape,
            colors
        )
    }
}

@Composable
fun BasicDialog(title: String, description: String, onClick: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = description,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorModalBottomSheet(
    description: String,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Đã xảy ra lỗi",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AppButton(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                text = "Đóng",
            )
        }
    }
}


@Composable
fun FoodItemCounter(
    modifier: Modifier = Modifier,
    onCounterIncrement: () -> Unit,
    onCounterDecrement: () -> Unit,
    count: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                onCounterIncrement.invoke()
            },
            modifier = Modifier

                .size(35.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(0.dp),


            ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(28.dp)
            )
        }


        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.height(20.dp),
            textAlign = TextAlign.Center
        )


        IconButton(
            onClick = {
                onCounterDecrement.invoke()
            },
            modifier = Modifier

                .size(35.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(color = MaterialTheme.colorScheme.onPrimary)
                .padding(0.dp),


            ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(28.dp)
            )
        }
    }
}

@Composable
fun MyFloatingActionButton(
    onClick: () -> Unit,
    bgColor: Color,
    content: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = bgColor,
    ) {
        content()
    }
}

@Composable
fun BoxScope.ItemCount(count: Int) {
    Box(
        modifier = Modifier
            .padding(6.dp)
            .background(MaterialTheme.colorScheme.error, shape = CircleShape)
            .padding(vertical = 4.dp, horizontal = 6.dp)
            .align(Alignment.TopEnd)
            .wrapContentSize(align = Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else "$count",
            modifier = Modifier
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = 8.sp)
        )
    }
}


@Composable
fun Loading(
    modifier: Modifier = Modifier,
    circleSize: Dp = 25.dp,
    circleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 20.dp
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 using LinearOutSlowInEasing
                        1.0f at 300 using LinearOutSlowInEasing
                        0.0f at 600 using LinearOutSlowInEasing
                        0.0f at 1200 using LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDistance.toPx() }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.CenterHorizontally)
    ) {
        circleValues.forEach { value ->
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        translationY = -value * distance
                    }
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    )
            )
        }
    }

}

@Composable
fun HeaderDefaultView(
    text: String,
    onBackIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
    onBack: (() -> Unit)? = null,
    icon: ImageVector? = null,
    iconClick: (() -> Unit)? = null,
    tintIcon: Color = MaterialTheme.colorScheme.primary,

    ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconCustomButton(
                onClick = onBack,
                icon = onBackIcon,
                containerColor = Color.Transparent,
                iconColor = MaterialTheme.colorScheme.primary
            )
        } else {
            Spacer(modifier = Modifier.weight(0.2f))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            color = tintIcon,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Center
        )

        if (icon != null && iconClick != null) {
            IconCustomButton(
                onClick = iconClick,
                icon = icon,
                containerColor = MaterialTheme.colorScheme.background,
                iconColor = tintIcon,

                )

        } else {
            Spacer(modifier = Modifier.weight(0.2f))
        }

    }


}

@Composable
fun IconCustomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    IconButton(
        onClick = {
            onClick.invoke()
        },
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = containerColor)
            .padding(4.dp),


        ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(28.dp)
        )
    }
}

@Composable
fun Retry(
    message: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Button(onClick = onClicked) {
            Text(text = "Tải lại")
        }

    }
}

@Composable
fun FoodAppDialog(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.scrim,
    message: String,
    messageColor: Color = MaterialTheme.colorScheme.outline,
    onDismiss: () -> Unit,
    containerConfirmButtonColor: Color = MaterialTheme.colorScheme.error,
    labelConfirmButtonColor: Color = MaterialTheme.colorScheme.onPrimary,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = "Ok",
    dismissText: String = "Đóng",
    showConfirmButton: Boolean = true,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = { Text(text = message, fontSize = 16.sp, color = messageColor, lineHeight = 24.sp) },
        containerColor = MaterialTheme.colorScheme.background,
        confirmButton = {
            if (showConfirmButton) {
                Button(
                    onClick = { onConfirm?.invoke(); onDismiss() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = containerConfirmButtonColor,
                        contentColor = labelConfirmButtonColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(confirmText)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline)
            ) {
                Text(text = dismissText, color = MaterialTheme.colorScheme.outline)
            }
        }
    )
}

@Composable
fun ThemeSwitcher(
    darkTheme: Boolean = false,
    size: Dp = 150.dp,
    iconSize: Dp = size / 4.5f,
    padding: Dp = 10.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit,
) {
    val offset by animateDpAsState(
        targetValue = if (darkTheme) 0.dp else size / 1.5f,
        animationSpec = animationSpec
    )

    Box(
        modifier = Modifier
            .width(size * 1.625f)
            .height(size)
            .clip(shape = parentShape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                ),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Nightlight,
                    contentDescription = "Theme Icon",
                    tint = if (darkTheme) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Theme Icon",
                    tint = if (darkTheme) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}

@Composable
fun CustomPagerIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPage: Int,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 6.dp,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.outline,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) dotSize * 1.5f else dotSize)
                    .clip(CircleShape)
                    .background(if (index == currentPage) selectedColor else unselectedColor)
            )
        }
    }
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val rows = if (data.isEmpty()) 0 else 1 + (data.count() - 1) / nColumns
    items(rows) { rowIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.count()) {
                    val item = data[itemIndex]
                    androidx.compose.runtime.key(key?.invoke(item)) {
                        Box(
                            modifier = Modifier.weight(1f, fill = true),
                            propagateMinConstraints = true
                        ) {
                            itemContent.invoke(this, item)
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}


fun <T : Any> LazyListScope.gridItems(
    data: LazyPagingItems<T>,
    nColumns: Int,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(T?) -> Unit,
    placeholderContent: @Composable BoxScope.() -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
        )
    },
) {
    val rowCount = if (data.itemCount == 0) 0 else (data.itemCount + nColumns - 1) / nColumns
    items(rowCount) { rowIndex ->
        Row(horizontalArrangement = horizontalArrangement) {
            for (columnIndex in 0 until nColumns) {
                val itemIndex = rowIndex * nColumns + columnIndex
                if (itemIndex < data.itemCount) {
                    val item = data[itemIndex]
                    Box(
                        modifier = Modifier.weight(1f, fill = true),
                        propagateMinConstraints = true
                    ) {
                        if (item != null) {
                            if (key != null) {
                                key(key(item)) {
                                    itemContent(this, item)
                                }
                            } else {
                                itemContent(this, item)
                            }
                        } else {
                            placeholderContent()
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f, fill = true))
                }
            }
        }
    }
}

@Composable
fun DetailsTextRow(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.outline,

    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> LazyPagingSample(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<T>,
    textNothing: String,
    iconNothing: ImageVector,
    onRetry: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    columns: Int = 1,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit,

    ) {
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val loadState = items.loadState
    val isScrolledToTop by remember { derivedStateOf {
        lazyListState.firstVisibleItemIndex == 0
                && lazyListState.firstVisibleItemScrollOffset == 0
    } }
    val pullState = rememberPullToRefreshState()
    Box(modifier = modifier.pullToRefresh(
        state = pullState,
        isRefreshing = isRefreshing,
        onRefresh = {
          coroutineScope.launch {
              isRefreshing = true
              onRefresh?.invoke()
              delay(1000L)
              isRefreshing = false
          }
        },
        enabled = isScrolledToTop
    )) {
        when {
            // 1. Lỗi khi load lần đầu
            loadState.refresh is LoadState.Error -> {
                val error = (loadState.refresh as LoadState.Error).error

                Retry(
                    message = error.localizedMessage ?: "Lỗi không xác định",
                    onClicked = {
                        items.retry()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 2. Đang loading lần đầu
            loadState.refresh is LoadState.Loading -> {
                Loading(
                    modifier = modifier.fillMaxSize()
                )
            }

            // 3. Danh sách rỗng
            items.itemSnapshotList.items.isEmpty() -> {
                Nothing(
                    text = textNothing,
                    icon = iconNothing,
                    modifier = Modifier.fillMaxSize().align(Alignment.Center)
                )
            }

            // 4. Bình thường: hiển thị danh sách
            else -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    gridItems(
                        data = items,
                        nColumns = columns,
                        key = key,
                        itemContent = { item ->
                            item?.let { itemContent(it) }
                        }
                    )

                    // 5. Hiển thị loading cuối trang
                    if (loadState.append is LoadState.Loading) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    // 6. Hiển thị lỗi cuối trang
                    if (loadState.append is LoadState.Error) {
                        val error = (loadState.append as LoadState.Error).error
                        item {
                            Retry(
                                message = error.localizedMessage ?: "Lỗi khi tải thêm",
                                onClicked = { items.retry() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary,
            isRefreshing = isRefreshing,
            state = pullState
        )
    }


}

@Composable
fun NoteInput(
    modifier: Modifier = Modifier.height(300.dp),
    note: String,
    onNoteChange: (String) -> Unit,
    maxLines: Int = 5,
    textHolder: String,
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        value = note,
        onValueChange = {
            onNoteChange(it)
        },
        readOnly = readOnly,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = textHolder,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        },
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(8.dp),
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    )
}

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    minimizedMaxLines: Int,
    style: TextStyle,
) {
    var expanded by remember { mutableStateOf(false) }
    var hasVisualOverflow by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            onTextLayout = { hasVisualOverflow = it.hasVisualOverflow },
            style = style
        )
        if (hasVisualOverflow) {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalAlignment = Alignment.Bottom
            ) {
                val lineHeightDp: Dp = with(LocalDensity.current) { style.lineHeight.toDp() }
                Spacer(
                    modifier = Modifier
                        .width(48.dp)
                        .height(lineHeightDp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
                Text(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(start = 4.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { expanded = !expanded }
                        ),
                    text = "Xem thêm",
                    color = MaterialTheme.colorScheme.primary,
                    style = style
                )
            }
        }
    }
}







package com.example.foodapp.ui.screen.notification

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.data.model.Notification
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.Retry
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                NotificationState.Event.ShowError -> {
                    showErrorSheet = true
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getNotifications()
    }
    LaunchedEffect(state.notifications.size) {
        if (state.notifications.isNotEmpty()) {
            listState.scrollToItem(state.notifications.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderDefaultView(
            text = "Thông báo"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
        ) {
            TextButton(
                onClick = {
                    viewModel.onAction(NotificationState.Action.ReadAllNotification)
                }
            ) {
                Text(
                    text = "Đánh dấu đã đọc tất cả",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
        when (state.notificationListState) {
            is NotificationState.NotificationListState.Loading -> {
                Loading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            is NotificationState.NotificationListState.Success -> {

                if (state.notifications.isEmpty()) {
                    Nothing(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        icon = Icons.Default.NotificationsOff,
                        text = "Không có thông báo nào"
                    )
                } else {
                    LazyColumn(reverseLayout = true, state = listState) {
                        items(items = state.notifications, key = { it.id }) {
                            NotificationItem(
                                notification = it,
                                onRead = {

                                    showNotificationDialog = true
                                    viewModel.onAction(
                                        NotificationState.Action.OnNotificationClicked(
                                            it
                                        )
                                    )
                                }

                            )
                        }
                    }
                }
            }

            is NotificationState.NotificationListState.Error -> {
                val errorMessage =
                    (state.notificationListState as NotificationState.NotificationListState.Error).message
                Retry(
                    message = errorMessage,
                    onClicked = {
                        viewModel.onAction(NotificationState.Action.Retry)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

        }

    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = state.error ?: "Đã có lỗi xảy ra",
            onDismiss = {
                showErrorSheet = false
            },
        )
    }
    if (showNotificationDialog) {
        FoodAppDialog(
            title = state.selectedNotification?.title ?: "",
            titleColor = MaterialTheme.colorScheme.primary,
            message = state.selectedNotification?.body ?: "",

            onDismiss = {
                showNotificationDialog = false
            },
            showConfirmButton = false
        )
    }
}

@Composable
fun NotificationItem(notification: Notification, onRead: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (notification.read) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else MaterialTheme.colorScheme.inversePrimary
            )
            .clickable { onRead() }
            .padding(16.dp)
    ) {
        Text(text = notification.title, style = MaterialTheme.typography.titleMedium)
        Text(text = notification.body, style = MaterialTheme.typography.bodySmall)
    }
}
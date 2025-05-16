package com.se114.foodapp.ui.screen.chat_box


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.util.TableInfo
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.ui.screen.components.NoteInput
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.gridItems


@Composable
fun ChatBoxScreen(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    viewModel: ChatBoxViewModel = hiltViewModel(),
) {
    val messageList = viewModel.messageList.collectAsLazyPagingItems()
    val tempMessages = viewModel.tempMessages.collectAsStateWithLifecycle()
    val message = viewModel.message.collectAsStateWithLifecycle()
    Dialog(
        onDismissRequest = onDismiss,
    ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface, // Explicitly name color parameter
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MessageList(
                    modifier = Modifier.weight(1f),
                    messageList = messageList,
                    tempMessages = tempMessages.value
                )
                MessageInput(
                    onMessageSend = {
                        viewModel.sendMessage(it)
                    },
                    message = message.value,
                    onMessageChange = {
                        viewModel.setMessage(it)
                    }
                )
            }

    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: LazyPagingItems<ChatMessage>,
    tempMessages: List<ChatMessage>,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messageList.itemCount, tempMessages.size) {
        listState.animateScrollToItem(0)
    }
    if (messageList.itemCount == 0 && messageList.loadState.refresh !is LoadState.Loading) {
        Nothing(
            text = "Hỏi tôi gì đó đi",
            icon = Icons.AutoMirrored.Filled.Message,
            modifier = modifier
        )
    } else {

        LazyColumn(
            modifier = modifier,
            reverseLayout = true,
            state = listState
        ) {
            items(tempMessages.reversed()) {
                MessageRow(chatMessage = it)
            }
            gridItems(
                data = messageList,
                nColumns = 1,
                key = { it.id!! },
                itemContent = { message ->
                    message?.let {
                        MessageRow(chatMessage = message)
                    }

                },
            )

        }
    }


}

@Composable
fun MessageRow(chatMessage: ChatMessage) {
    val isSystem = chatMessage.sender == "BOT"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSystem) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = if (isSystem) 8.dp else 40.dp,
                    end = if (isSystem) 40.dp else 8.dp
                )
                .clip(RoundedCornerShape(18.dp))
                .background(if (isSystem) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            SelectionContainer {
                Text(
                    text = chatMessage.content,
                    fontWeight = FontWeight.W500,
                    color = if (isSystem) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                )
            }
        }


    }


}


@Composable
fun MessageInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onMessageSend: (String) -> Unit,
) {


    Row(
verticalAlignment = Alignment.CenterVertically
        ) {
        NoteInput(
            modifier = Modifier.weight(0.6f),
            note = message,
            onNoteChange = {
                onMessageChange(it)
            },
            maxLines = 1,
            textHolder = "Nhập câu hỏi...",
        )
        IconButton(
            onClick = {
                if (message.isNotEmpty()) {
                    val messageSend = message.trim()
                    onMessageSend(messageSend)
                    onMessageChange("")
                }
            }, modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
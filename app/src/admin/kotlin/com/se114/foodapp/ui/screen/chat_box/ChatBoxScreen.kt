package com.se114.foodapp.ui.screen.chat_box

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.ChatKnowledgeEntry
import com.example.foodapp.data.model.IntentType
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.Retry


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBoxScreen(
    navController: NavController,
    viewModel: ChatBoxViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleteIntentType by remember { mutableStateOf(false) }

    var isEditIntentTypeMode by remember { mutableStateOf(false) }
    var isEditChatKnowledgeEntryMode by remember { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                ChatBoxState.Event.ShowError -> {
                    showErrorSheet = true
                }
                ChatBoxState.Event.OnBack -> {
                    navController.popBackStack()
                }
                is ChatBoxState.Event.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getChatKnowledgeEntry()
        viewModel.getIntentTypes()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(ChatBoxState.Action.OnBack)
            },
            text = "Chat Box"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Loại ý định",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (!isEditChatKnowledgeEntryMode) {
                        AnimatedContent(
                            targetState = isEditIntentTypeMode,
                            transitionSpec = {
                                (slideInVertically { it } + fadeIn()) togetherWith
                                        (slideOutVertically { -it } + fadeOut())
                            },
                            label = "Edit IntentType Switch"
                        ) { isEdit ->
                            if (!isEdit) {
                                IconButton(
                                    onClick = {
                                        viewModel.onAction(
                                            ChatBoxState.Action.OnEditState(false)
                                        )
                                        viewModel.onAction(ChatBoxState.Action.OnIntentTypeSelected(IntentType()))
                                        isEditIntentTypeMode = true

                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .padding(0.dp)

                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add IntentType",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }}


                }
                AnimatedContent(
                    targetState = isEditIntentTypeMode,
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn()) togetherWith
                                (slideOutVertically { -it } + fadeOut())
                    },
                ) { isEdit ->
                    if (isEdit) {
                        EditIntentTypeCard(
                            onModify = {
                                if (uiState.isUpdating)
                                    viewModel.onAction(ChatBoxState.Action.UpdateIntentType)
                                else viewModel.onAction(ChatBoxState.Action.CreateIntentType)
                                isEditIntentTypeMode = false
                            },
                            value = uiState.intentTypeSelected.name,
                            onValueChange = { newName ->
                                viewModel.onAction(ChatBoxState.Action.OnNameChanged(newName))
                            },

                            onDelete = {
                                isDeleteIntentType = true
                                showDeleteDialog = true
                            },
                            isUpdating = uiState.isUpdating,
                            modifier = Modifier.fillMaxWidth(),
                            onClose = {
                                viewModel.onAction(ChatBoxState.Action.OnIntentTypeSelected(IntentType()))
                                isEditIntentTypeMode = false
                            },
                        )
                    }

                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) {
                    when(uiState.intentTypeListState){
                        is ChatBoxState.IntentTypeList.Error -> {
                            val error = (uiState.intentTypeListState as ChatBoxState.IntentTypeList.Error).message
                            Retry(
                                message = error,
                                onClicked = {
                                    viewModel.getIntentTypes()
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ChatBoxState.IntentTypeList.Loading -> {
                            Loading(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ChatBoxState.IntentTypeList.Success -> {
                            if(uiState.intentTypeList.isEmpty()){
                                Nothing(
                                    icon = Icons.Default.Queue,
                                    iconSize = 24.dp,
                                    text = "Không có loại ý định nào",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }else{
                                ChipsGroupWrap(
                                    options = uiState.intentTypeList.map { it.name },
                                    modifier = Modifier.padding(8.dp),
                                    selectedOption = uiState.intentTypeSelected.name,
                                    onOptionSelected = { selectedName ->
                                        val selectedIntentType =
                                            uiState.intentTypeList.find { it.name == selectedName }
                                        if (selectedIntentType != null) {
                                            viewModel.onAction(ChatBoxState.Action.OnIntentTypeSelected(selectedIntentType))
                                            viewModel.onAction(ChatBoxState.Action.OnEditState(true))
                                            isEditIntentTypeMode = true
                                        }


                                    },
                                    thresholdExpend = 10,
                                    containerColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f),

                                    shouldSelectDefaultOption = false,
                                )
                            }
                        }
                    }
                }


            }
            Spacer(modifier = Modifier.size(100.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kiến thức",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (!isEditIntentTypeMode) {
                        AnimatedContent(
                            targetState = isEditChatKnowledgeEntryMode,
                            transitionSpec = {
                                (slideInVertically { it } + fadeIn()) togetherWith
                                        (slideOutVertically { -it } + fadeOut())
                            },
                            label = "Edit ChatKnowledge Switch"
                        ) { isEdit ->
                            if (!isEdit) {
                                IconButton(
                                    onClick = {
                                        viewModel.onAction(
                                            ChatBoxState.Action.OnEditState(false)
                                        )
                                        viewModel.onAction(ChatBoxState.Action.OnChatKnowledgeEntrySelected(ChatKnowledgeEntry()))
                                        isEditChatKnowledgeEntryMode = true

                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(color = MaterialTheme.colorScheme.outline)
                                        .padding(0.dp)

                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add ChatKnowledge",
                                        modifier = Modifier.size(30.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }}


                }
                AnimatedContent(
                    targetState = isEditChatKnowledgeEntryMode,
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn()) togetherWith
                                (slideOutVertically { -it } + fadeOut())
                    },
                ) { isEdit ->
                    if (isEdit) {
                        EditChatKnowledgeEntryCard(
                            onModify = {
                                if (uiState.isUpdating)
                                    viewModel.onAction(ChatBoxState.Action.UpdateChatKnowledgeEntry)
                                else viewModel.onAction(ChatBoxState.Action.CreateChatKnowledgeEntry)
                                isEditChatKnowledgeEntryMode = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            onClose = {
                                viewModel.onAction(ChatBoxState.Action.OnChatKnowledgeEntrySelected(ChatKnowledgeEntry()))
                                isEditChatKnowledgeEntryMode = false
                            },
                            onDelete = {
                                isDeleteIntentType = false
                                showDeleteDialog = true
                            },
                            isUpdating = uiState.isUpdating,
                            chatKnowledgeEntry = uiState.chatKnowledgeEntrySelected,
                            onTitleChange ={
                                viewModel.onAction(ChatBoxState.Action.OnTitleChanged(it))
                            },
                            onContentChange = {
                                viewModel.onAction(ChatBoxState.Action.OnContentChanged(it))
                            },
                            onPositionSelected = { selected ->
                                val selectedIntentType = uiState.intentTypeList.find { it.name == selected }
                                selectedIntentType?.let {
                                    viewModel.onAction(ChatBoxState.Action.OnIntentTypeChanged(it))
                                }
                            },
                            intentTypes = uiState.intentTypeList,
                        )
                    }

                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) {
                    when(uiState.chatKnowLedgeListState){
                        is ChatBoxState.ChatKnowLedgeList.Error -> {
                            val error = (uiState.chatKnowLedgeListState as ChatBoxState.ChatKnowLedgeList.Error).message
                            Retry(
                                message = error,
                                onClicked = {
                                    viewModel.getChatKnowledgeEntry()
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ChatBoxState.ChatKnowLedgeList.Loading -> {
                            Loading(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ChatBoxState.ChatKnowLedgeList.Success -> {
                            if(uiState.chatKnowLedgeList.isEmpty()){
                                Nothing(
                                    icon = Icons.Default.Interests,
                                    iconSize = 24.dp,
                                    text = "Không có loại kiến thức nào",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }else{
                                ChipsGroupWrap(
                                    options = uiState.chatKnowLedgeList.map { it.title },
                                    modifier = Modifier.padding(8.dp),
                                    selectedOption = uiState.chatKnowledgeEntrySelected.title,
                                    onOptionSelected = { selected ->
                                        val selectedChatKnowledgeEntry =
                                            uiState.chatKnowLedgeList.find { it.title == selected }
                                        if (selectedChatKnowledgeEntry != null) {
                                            viewModel.onAction(ChatBoxState.Action.OnChatKnowledgeEntrySelected(selectedChatKnowledgeEntry))
                                            viewModel.onAction(ChatBoxState.Action.OnEditState(true))
                                            isEditChatKnowledgeEntryMode = true
                                        }


                                    },
                                    thresholdExpend = 10,
                                    containerColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.5f),

                                    shouldSelectDefaultOption = false,
                                )
                            }
                        }
                    }
                }


            }
        }
    }
    if (showDeleteDialog) {
        FoodAppDialog(
            title = "Xóa ${if (isDeleteIntentType) "loại ý định" else "kiến thức"}",
            message = "Bạn có chắc chắn muốn xóa ${if (isDeleteIntentType) "loại ý định" else "kiến thức"} này không?",
            onDismiss = {

                showDeleteDialog = false
            },
            onConfirm = {

                if (isDeleteIntentType) {
                    viewModel.onAction(ChatBoxState.Action.DeleteIntentType)
                } else {
                    viewModel.onAction(ChatBoxState.Action.DeleteChatKnowledgeEntry)
                }
                isEditIntentTypeMode = false
                isEditChatKnowledgeEntryMode = false
                showDeleteDialog = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }



    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            },
        )
    }

}

@Composable
fun EditIntentTypeCard(
    modifier: Modifier = Modifier,
    onModify: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    isUpdating: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,


    ) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),

        ) {
        FoodAppTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()

        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onModify.invoke()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
            if (isUpdating) {

                IconButton(
                    onClick = {
                        onDelete.invoke()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.error)
                        .padding(0.dp),


                    ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }




            IconButton(
                onClick = {
                    onClose.invoke()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.outline)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }
    }
}


@Composable
fun EditChatKnowledgeEntryCard(

    modifier: Modifier = Modifier,
    onModify: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    isUpdating: Boolean = false,
    chatKnowledgeEntry: ChatKnowledgeEntry,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onPositionSelected: (String?) -> Unit,
    intentTypes: List<IntentType>,


    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FoodAppTextField(
                value = chatKnowledgeEntry.title,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .weight(1f),
                placeholder = {
                    Text(text = "Tiêu đề", color = MaterialTheme.colorScheme.outline)
                }
            )
            FoodAppTextField(
                value = chatKnowledgeEntry.content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .weight(1f),
                placeholder = {
                    Text(text = "Nội dung", color = MaterialTheme.colorScheme.outline)
                }
            )


        }
        ComboBoxSample(
            modifier = Modifier
                .fillMaxWidth(),
            textPlaceholder = "...",
            selected = intentTypes.find { it.id == chatKnowledgeEntry.id }?.name,
            onPositionSelected = onPositionSelected,
            options = intentTypes.map { it.name },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        ) {
            IconButton(
                onClick = {
                    onModify.invoke()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
            if (isUpdating) {

                IconButton(
                    onClick = {
                        onDelete.invoke()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.error)
                        .padding(0.dp),


                    ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .size(28.dp)
                    )
                }
            }
            IconButton(
                onClick = {
                    onClose.invoke()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.outline)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                )
            }


        }
    }


}


package com.se114.foodapp.ui.screen.chat_box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.ChatKnowledgeEntryRequest
import com.example.foodapp.data.dto.request.IntentTypeRequest
import com.example.foodapp.data.model.ChatKnowledgeEntry
import com.example.foodapp.data.model.IntentType
import com.se114.foodapp.domain.use_case.ai.CreateChatKnowUseCase
import com.se114.foodapp.domain.use_case.ai.CreateIntentTypeUseCase
import com.se114.foodapp.domain.use_case.ai.DeleteChatKnowUseCase
import com.se114.foodapp.domain.use_case.ai.DeleteIntentTypeUseCase
import com.se114.foodapp.domain.use_case.ai.GetChatKnowledgeEntryUseCase
import com.se114.foodapp.domain.use_case.ai.GetIntentTypesUseCase
import com.se114.foodapp.domain.use_case.ai.UpdateChatKnowUseCase
import com.se114.foodapp.domain.use_case.ai.UpdateIntentTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBoxViewModel @Inject constructor(
    private val getChatKnowledgeEntryUseCase: GetChatKnowledgeEntryUseCase,
    private val createChatKnowUseCase: CreateChatKnowUseCase,
    private val updateChatKnowUseCase: UpdateChatKnowUseCase,
    private val deleteChatKnowUseCase: DeleteChatKnowUseCase,
    private val getIntentTypesUseCase: GetIntentTypesUseCase,
    private val createIntentTypeUseCase: CreateIntentTypeUseCase,
    private val updateIntentTypeUseCase: UpdateIntentTypeUseCase,
    private val deleteIntentTypeUseCase: DeleteIntentTypeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatBoxState.UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ChatBoxState.Event>()
    val event = _event.receiveAsFlow()

    fun getChatKnowledgeEntry() {
        viewModelScope.launch {
            getChatKnowledgeEntryUseCase().collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                chatKnowLedgeList = response.data,
                                chatKnowLedgeListState = ChatBoxState.ChatKnowLedgeList.Success
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                chatKnowLedgeListState = ChatBoxState.ChatKnowLedgeList.Error(
                                    response.errorMessage
                                )
                            )
                        }

                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                chatKnowLedgeListState = ChatBoxState.ChatKnowLedgeList.Loading
                            )
                        }

                    }
                }
            }
        }
    }

    fun getIntentTypes() {
        viewModelScope.launch {
            getIntentTypesUseCase().collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                intentTypeList = response.data,
                                intentTypeListState = ChatBoxState.IntentTypeList.Success
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                intentTypeListState = ChatBoxState.IntentTypeList.Error(response.errorMessage)
                            )
                        }

                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                intentTypeListState = ChatBoxState.IntentTypeList.Loading
                            )
                        }
                    }
                }
            }
        }

    }

    private fun createChatKnowledgeEntry() {
        viewModelScope.launch {
            createChatKnowUseCase(
                ChatKnowledgeEntryRequest(
                    title = _uiState.value.chatKnowledgeEntrySelected.title,
                    content = _uiState.value.chatKnowledgeEntrySelected.content,
                    intentTypeId = _uiState.value.intentTypeSelected.id!!
                )
            ).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                chatKnowLedgeList = it.chatKnowLedgeList + response.data,
                                loading = false,
                                error = null
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowToast("Thêm mới thành công"))
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true,
                            )
                        }
                    }
                }

            }
        }
    }

    private fun updateChatKnowledgeEntry() {
        viewModelScope.launch {
            updateChatKnowUseCase(
                _uiState.value.chatKnowledgeEntrySelected.id!!,
                ChatKnowledgeEntryRequest(
                    title = _uiState.value.chatKnowledgeEntrySelected.title,
                    content = _uiState.value.chatKnowledgeEntrySelected.content,
                    intentTypeId = _uiState.value.intentTypeSelected.id!!
                )
            ).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = null,
                                chatKnowLedgeList = it.chatKnowLedgeList.map { chatKnowledgeEntry ->
                                    if (chatKnowledgeEntry.id == response.data.id) {
                                        response.data
                                    } else {
                                        chatKnowledgeEntry
                                    }
                                }
                            )

                        }
                        _event.send(ChatBoxState.Event.ShowToast("Cập nhật thành công"))
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteChatKnowledgeEntry() {
        viewModelScope.launch {
            deleteChatKnowUseCase(_uiState.value.chatKnowledgeEntrySelected.id!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = null,
                                chatKnowLedgeList = it.chatKnowLedgeList.filter { chatKnowledgeEntry ->
                                    chatKnowledgeEntry.id != _uiState.value.chatKnowledgeEntrySelected.id
                                })

                        }
                        _event.send(ChatBoxState.Event.ShowToast("Xóa thành công"))

                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createIntentType() {
        viewModelScope.launch {
            createIntentTypeUseCase(
                IntentTypeRequest(
                    name = _uiState.value.intentTypeSelected.name
                )
            ).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                intentTypeList = it.intentTypeList + response.data,
                                loading = false,
                                error = null
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowToast("Thêm mới thành công"))
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = response.errorMessage
                            )
                        }

                        _event.send(ChatBoxState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateIntentType() {
        viewModelScope.launch {
            updateIntentTypeUseCase(
                _uiState.value.intentTypeSelected.id!!,
                IntentTypeRequest(
                    name = _uiState.value.intentTypeSelected.name
                )
            ).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = null,
                                intentTypeList = it.intentTypeList.map { intentType ->
                                    if (intentType.id == response.data.id) {
                                        response.data
                                    } else {
                                        intentType
                                    }
                                }

                            )
                        }
                        _event.send(ChatBoxState.Event.ShowToast("Cập nhật thành công"))
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteIntentType() {
        viewModelScope.launch {
            deleteIntentTypeUseCase(_uiState.value.intentTypeSelected.id!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = null,
                                intentTypeList = it.intentTypeList.filter { intentType ->
                                    intentType.id != _uiState.value.intentTypeSelected.id
                                })
                        }
                        _event.send(ChatBoxState.Event.ShowToast("Xóa thành công"))

                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ChatBoxState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: ChatBoxState.Action) {
        when (action) {
            is ChatBoxState.Action.CreateChatKnowledgeEntry -> {
                createChatKnowledgeEntry()
            }

            is ChatBoxState.Action.UpdateChatKnowledgeEntry -> {
                updateChatKnowledgeEntry()
            }

            is ChatBoxState.Action.DeleteChatKnowledgeEntry -> {
                deleteChatKnowledgeEntry()
            }

            is ChatBoxState.Action.CreateIntentType -> {
                createIntentType()
            }

            is ChatBoxState.Action.UpdateIntentType -> {
                updateIntentType()
            }

            is ChatBoxState.Action.DeleteIntentType -> {
                deleteIntentType()
            }

            is ChatBoxState.Action.OnChatKnowledgeEntrySelected -> {
                _uiState.update {
                    it.copy(
                        chatKnowledgeEntrySelected = action.chatKnowledgeEntry
                    )
                }
            }

            is ChatBoxState.Action.OnIntentTypeSelected -> {
                _uiState.update {
                    it.copy(
                        intentTypeSelected = action.intentType
                    )
                }
            }

            is ChatBoxState.Action.OnTitleChanged -> {
                _uiState.update {
                    it.copy(
                        chatKnowledgeEntrySelected = it.chatKnowledgeEntrySelected.copy(
                            title = action.title
                        )
                    )
                }
            }

            is ChatBoxState.Action.OnContentChanged -> {
                _uiState.update {
                    it.copy(
                        chatKnowledgeEntrySelected = it.chatKnowledgeEntrySelected.copy(
                            content = action.content
                        )
                    )
                }
            }

            is ChatBoxState.Action.OnNameChanged -> {
                _uiState.update {
                    it.copy(
                        intentTypeSelected = it.intentTypeSelected.copy(
                            name = action.name
                        )
                    )
                }
            }

            ChatBoxState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(ChatBoxState.Event.OnBack)
                }
            }

            is ChatBoxState.Action.OnEditState -> {
                _uiState.update {
                    it.copy(
                        isUpdating = action.isUpdating
                    )
                }
            }

            is ChatBoxState.Action.OnIntentTypeChanged -> {
                _uiState.update {
                    it.copy(
                        chatKnowledgeEntrySelected = it.chatKnowledgeEntrySelected.copy(
                            intentType = action.intentType
                        )
                    )
                }
            }
        }

    }


}

object ChatBoxState {
    data class UiState(
        val chatKnowledgeEntrySelected: ChatKnowledgeEntry = ChatKnowledgeEntry(),
        val intentTypeSelected: IntentType = IntentType(),
        val loading: Boolean = false,
        val error: String? = null,
        val chatKnowLedgeList: List<ChatKnowledgeEntry> = emptyList(),
        val intentTypeList: List<IntentType> = emptyList(),
        val chatKnowLedgeListState: ChatKnowLedgeList = ChatKnowLedgeList.Loading,
        val intentTypeListState: IntentTypeList = IntentTypeList.Loading,
        val isUpdating: Boolean = false,
    )

    sealed interface ChatKnowLedgeList {
        data object Success : ChatKnowLedgeList
        data class Error(val message: String) : ChatKnowLedgeList
        object Loading : ChatKnowLedgeList

    }

    sealed interface IntentTypeList {
        data object Success : IntentTypeList
        data class Error(val message: String) : IntentTypeList
        object Loading : IntentTypeList
    }

    sealed interface Event {
        data class ShowToast(val message: String) : Event
        data object ShowError : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object CreateChatKnowledgeEntry : Action
        data object UpdateChatKnowledgeEntry : Action
        data object DeleteChatKnowledgeEntry : Action
        data object CreateIntentType : Action
        data object UpdateIntentType : Action
        data object DeleteIntentType : Action
        data class OnChatKnowledgeEntrySelected(val chatKnowledgeEntry: ChatKnowledgeEntry) : Action
        data class OnIntentTypeSelected(val intentType: IntentType) : Action
        data class OnTitleChanged(val title: String) : Action
        data class OnContentChanged(val content: String) : Action
        data class OnIntentTypeChanged(val intentType: IntentType) : Action
        data class OnNameChanged(val name: String) : Action
        data object OnBack : Action
        data class OnEditState(val isUpdating: Boolean) : Action


    }
}
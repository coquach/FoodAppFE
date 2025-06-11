package com.se114.foodapp.ui.screen.chat_box

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.ChatMessage
import com.se114.foodapp.domain.use_case.ai.GetMessageChatUseCase
import com.se114.foodapp.domain.use_case.ai.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBoxViewModel @Inject constructor(
    private val getMessageChatUseCase: GetMessageChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading.value
    fun setMessage(value: String) {
        _message.value = value
    }




    private val _messageList = MutableStateFlow<PagingData<ChatMessage>>(PagingData.empty())
    val messageList = _messageList

    private val _tempMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val tempMessages = _tempMessages.asStateFlow()

    init {
        getMessageList()
    }

    fun getMessageList() {
        viewModelScope.launch {
            getMessageChatUseCase().cachedIn(viewModelScope).collect {
                _messageList.value = it
            }
        }
    }

    private fun resetTempMessages() {
        _tempMessages.update { emptyList() }
    }
    val botTypingMessage = ChatMessage(
        content = "Đang nhập...",
        sender = "BOT",
        id = 2
    )

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                resetTempMessages()
                val userMessage = ChatMessage(
                    content = message,
                    sender = "USER",
                    id = 1
                )

               sendMessageUseCase(message).collect { response ->
                   when (response) {
                       is ApiResponse.Success -> {
                           _isLoading.value = false
                           resetTempMessages()
                           getMessageList()
                       }

                       is ApiResponse.Failure -> {
                           _tempMessages.update { oldList ->
                               oldList.map { if(it.id == 2L) it.copy(content = response.errorMessage) else it }
                           }
                       }

                       is ApiResponse.Loading -> {
                            _isLoading.value = true
                           _tempMessages.update { oldList ->
                               oldList + userMessage + botTypingMessage
                           }
                       }
                   }
               }


            } catch (e: Exception) {
                e.printStackTrace()
                _tempMessages.update { oldList ->
                    oldList.map { if(it.id == 2L) it.copy(content = "Lỗi không xác định") else it }
                }


            }
        }
    }


}
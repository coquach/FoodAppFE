package com.se114.foodapp.ui.screen.chat_box

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertFooterItem
import androidx.paging.insertHeaderItem
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.repository.ChatBoxRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBoxViewModel @Inject constructor(
    private val chatBoxRepository: ChatBoxRepository,
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

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
            chatBoxRepository.getAllMessageChat().cachedIn(viewModelScope).collect {
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
                _tempMessages.update { oldList ->
                    oldList + userMessage + botTypingMessage
                }
                val response = chatBoxRepository.sendMessage(message)

                when (response) {
                    is ApiResponse.Success -> {
                        resetTempMessages()
                        getMessageList()
                    }

                    is ApiResponse.Error -> {
                        _tempMessages.update { oldList ->
                            oldList.map { if(it.id == 2L) it.copy(content = response.message) else it }
                        }
                    }

                    else -> {
                        _tempMessages.update { oldList ->
                            oldList.map { if(it.id == 2L) it.copy(content = "Lỗi không xác định") else it }
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
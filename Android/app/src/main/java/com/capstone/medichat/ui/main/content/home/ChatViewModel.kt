package com.capstone.medichat.ui.main.content.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<MutableList<ChatMessage>>().apply {
        value = mutableListOf()
    }
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    fun addMessage(message: ChatMessage) {
        _messages.value?.add(message)
        _messages.postValue(_messages.value)
    }
}

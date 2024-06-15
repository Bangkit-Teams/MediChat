package com.capstone.medichat.ui.main.content.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<MutableList<ChatMessage>>().apply {
        value = mutableListOf()
    }
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    // Add message and return its position
    fun addMessage(message: ChatMessage): Int {
        val currentList = _messages.value ?: mutableListOf()
        currentList.add(message)
        _messages.value = currentList
        return currentList.size - 1 // Return the position of the new message
    }

    // Update message at a specific position
    fun updateMessage(position: Int, newMessage: String) {
        val currentList = _messages.value ?: return
        if (position in currentList.indices) {
            currentList[position] = ChatMessage(newMessage, isUserMessage = false) // Assuming only AI messages are updated
            _messages.value = currentList
        }
    }
}

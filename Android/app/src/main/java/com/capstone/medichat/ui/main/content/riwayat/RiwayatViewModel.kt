package com.capstone.medichat.ui.main.content.riwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.medichat.data.database.ChatSaveMessage
import com.capstone.medichat.data.database.ChatDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RiwayatViewModel(private val chatDatabase: ChatDatabase) : ViewModel() {

    val allRiwayatMessages: LiveData<List<ChatSaveMessage>> = chatDatabase.chatSaveMessageDao().getAllMessagesLiveData()

    fun deleteMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            chatDatabase.chatSaveMessageDao().deleteAllMessages()
        }
    }
}

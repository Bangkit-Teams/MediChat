package com.capstone.medichat.data.repository

import com.capstone.medichat.data.database.ChatSaveMessage
import com.capstone.medichat.data.database.ChatSaveMessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(private val chatSaveMessageDao: ChatSaveMessageDao) {

    suspend fun saveChatMessage(message: ChatSaveMessage) {
        withContext(Dispatchers.IO) {
            chatSaveMessageDao.insert(message)
        }
    }

    fun getAllMessages(): Flow<List<ChatSaveMessage>> {
        return chatSaveMessageDao.getAllMessages()
    }

    suspend fun clearAllMessages() {
        withContext(Dispatchers.IO) {
            chatSaveMessageDao.deleteAllMessages()
        }
    }
}

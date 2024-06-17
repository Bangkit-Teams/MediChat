package com.capstone.medichat.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSaveMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatSaveMessage: ChatSaveMessage)

    @Query("SELECT * FROM chat_save_messages")
    fun getAllMessages(): Flow<List<ChatSaveMessage>>

    @Query("SELECT * FROM chat_save_messages")
    fun getAllMessagesLiveData(): LiveData<List<ChatSaveMessage>>

    @Query("DELETE FROM chat_save_messages")
    suspend fun deleteAllMessages()
}

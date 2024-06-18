package com.capstone.medichat.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_save_messages")
data class ChatSaveMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val message: String,
    val isUserMessage: Boolean
)

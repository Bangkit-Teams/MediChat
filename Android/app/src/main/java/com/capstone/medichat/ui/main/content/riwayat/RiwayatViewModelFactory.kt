package com.capstone.medichat.ui.main.content.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.medichat.data.database.ChatDatabase

class RiwayatViewModelFactory(private val chatDatabase: ChatDatabase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiwayatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RiwayatViewModel(chatDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

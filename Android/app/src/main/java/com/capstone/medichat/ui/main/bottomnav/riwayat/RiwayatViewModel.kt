package com.capstone.medichat.ui.main.bottomnav.riwayat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RiwayatViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Riwayat Fragment"
    }
    val text: LiveData<String> = _text
}
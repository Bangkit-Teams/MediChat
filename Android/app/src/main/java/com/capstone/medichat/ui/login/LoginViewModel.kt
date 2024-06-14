package com.capstone.medichat.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.medichat.data.preference.UserModel
import com.capstone.medichat.data.repo.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {


    fun login(email: String, password: String) = repository.login(email, password)
}
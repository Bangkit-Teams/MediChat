package com.capstone.medichat.ui.register

import androidx.lifecycle.ViewModel
import com.capstone.medichat.data.repo.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    fun signup(name: String, email: String, password: String) = repository.signup(name, email, password)
}
package com.capstone.medichat.data.preference

data class UserModel(
    val email: String,
    val token: String,
    val password: String,
    val isLogin: Boolean = false
)

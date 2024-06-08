package com.capstone.medichat.data.repo

import android.content.Context
import com.capstone.medichat.data.api.ApiConfig
import com.capstone.medichat.data.preference.UserPreference
import com.capstone.medichat.data.preference.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(apiService, pref)
    }
}
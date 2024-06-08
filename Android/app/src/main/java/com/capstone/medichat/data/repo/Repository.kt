package com.capstone.medichat.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.medichat.data.api.ApiService
import com.capstone.medichat.data.preference.UserModel
import com.capstone.medichat.data.preference.UserPreference
import com.capstone.medichat.data.response.ErrorResponse
import com.capstone.medichat.data.response.LoginResponse
import com.capstone.medichat.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun login(email: String,password: String): LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.login(email,password)
            emit(Result.Success(response))
        }catch (e: HttpException){
            val error = e.response()?.errorBody()?.string()
            val body = Gson().fromJson(error, ErrorResponse::class.java)
            emit(Result.Error(body.message))
        }
    }

    fun signup(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.signup(name, email, password)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }


    companion object{
        private var INSTANCE: Repository? = null

        fun clearInstance(){
            INSTANCE = null
        }

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreference
        ): Repository =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: Repository(apiService,userPreferences)
            }.also { INSTANCE = it }
    }
}
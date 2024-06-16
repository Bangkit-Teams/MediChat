package com.capstone.medichat.data.api

import com.capstone.medichat.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun getApiService(token: String): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeaders)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    // New Retrofit instance for the additional service
    private val additionalClient = OkHttpClient.Builder()
        .connectTimeout(200, TimeUnit.SECONDS)  // Set connect timeout
        .readTimeout(200, TimeUnit.SECONDS)     // Set read timeout
        .writeTimeout(200, TimeUnit.SECONDS)    // Set write timeout
        .build()

    private val additionalRetrofit = Retrofit.Builder()
        .baseUrl("https://generate-response1-t7yc42rinq-uc.a.run.app/")
        .client(additionalClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val additionalService: ApiService = additionalRetrofit.create(ApiService::class.java)

    // New Retrofit instance for the recommendation service
    private val recommendationClient = OkHttpClient.Builder()
        .connectTimeout(200, TimeUnit.SECONDS)  // Set connect timeout
        .readTimeout(200, TimeUnit.SECONDS)     // Set read timeout
        .writeTimeout(200, TimeUnit.SECONDS)    // Set write timeout
        .build()

    private val recommendationRetrofit = Retrofit.Builder()
        .baseUrl("https://recommendation-t7yc42rinq-uc.a.run.app/")
        .client(recommendationClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val recommendationService: ApiService = recommendationRetrofit.create(ApiService::class.java)
}

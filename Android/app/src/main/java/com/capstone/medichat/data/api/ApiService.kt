    package com.capstone.medichat.data.api


    import com.capstone.medichat.data.response.LoginResponse
    import com.capstone.medichat.data.response.RegisterResponse
    import okhttp3.RequestBody
    import okhttp3.ResponseBody
    import retrofit2.Call
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.Field
    import retrofit2.http.FormUrlEncoded
    import retrofit2.http.Headers
    import retrofit2.http.POST

    interface ApiService {
        @FormUrlEncoded
        @POST("register")
        suspend fun signup(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("password") password: String,
        ): RegisterResponse

        @FormUrlEncoded
        @POST("login")
        suspend fun login(
            @Field("email") email: String,
            @Field("password") password: String,
        ): LoginResponse

        @Headers("Content-Type: application/json")
        @POST("llama")
        fun sendMessage(@Body body: RequestBody): Call<ResponseBody>

        @Headers("Content-Type: application/json")
        @POST("/recommendation")
        suspend fun postRecommendation(@Body body: RequestBody): Response<ResponseBody>


    }
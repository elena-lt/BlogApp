package com.data.network.auth

import com.data.models.LoginResponse
import com.data.models.RegistrationResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OpenApiAuthService {

    @POST ("account/login")
    @FormUrlEncoded
    suspend fun login (
        @Field ("username") username: String,
        @Field ("password") password: String,
    ) : LoginResponse

    @POST("account/register")
    @FormUrlEncoded
    suspend fun register (
        @Field ("email") email: String,
        @Field ("username") username: String,
        @Field ("password") password: String,
        @Field ("password2") password2: String

    ): RegistrationResponse
}
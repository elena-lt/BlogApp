package com.data.network.main

import androidx.lifecycle.LiveData
import com.data.models.AccountProperties
import com.data.models.GenericResponse
import com.data.utils.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

    @GET ("account/properties")
    fun getAccountProperties(
        @Header ("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT ("account/properties/update")
    @FormUrlEncoded
    fun updateAccountProperties(
        @Header ("Authorization") authorization: String,
        @Field("email") email: String,
        @Field ("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>
}
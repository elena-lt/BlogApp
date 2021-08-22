package com.data.network.main

import com.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    suspend fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): AccountProperties

    @PUT("account/properties/update")
    @FormUrlEncoded
    suspend fun updateAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): GenericResponse

    @PUT("account/change_password/")
    @FormUrlEncoded
    fun changePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String,
    ): GenericResponse

    @GET("blog/list")
    suspend fun searchListBlogPost(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("ordering") ordering: String,
        @Query("page") page: Int,
    ): BlogSearchResponse

    @GET ("blog/{slug}/is_author")
    suspend fun isAuthor(
        @Header("Authorization") authorization: String,
        @Path ("slug") slug: String
    ): GenericResponse

    @Multipart
    @POST ("blog/create")
    suspend fun createBlogPost(
        @Header("Authorization") authorization: String,
        @Part ("title") title: RequestBody,
        @Part ("body") body: RequestBody,
        @Part  image: MultipartBody.Part?
    ): BlogCreateUpdateResponse

    @Multipart
    @PUT ("blog/{slug}/update")
    suspend fun updateBlogPost(
        @Header("Authorization") authorization: String,
        @Path ("slug") slug: String,
        @Part ("title") title: RequestBody,
        @Part ("body") body: RequestBody,
        @Part  image: MultipartBody.Part?
    ): BlogCreateUpdateResponse

    @DELETE ("blog/{slug}/delete")
    suspend fun deleteBlogPost (
        @Header("Authorization") authorization: String,
        @Path ("slug") slug: String
    ): GenericResponse

}
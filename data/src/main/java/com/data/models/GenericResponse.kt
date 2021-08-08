package com.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenericResponse
    (
    @SerializedName("response")
    @Expose
    val response: String
)
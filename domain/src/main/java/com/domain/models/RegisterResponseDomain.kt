package com.domain.models

data class RegisterResponseDomain(

    var response: String,
    var errorMessage: String,
    var email: String,
    var username: String,
    var pk: Int,
    var token: String
)
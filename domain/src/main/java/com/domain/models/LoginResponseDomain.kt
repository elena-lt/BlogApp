package com.domain.models

data class LoginResponseDomain(

    var response: String,
    var errorMessage: String,
    var token: String,
    var pk: Int,
    var email: String
)
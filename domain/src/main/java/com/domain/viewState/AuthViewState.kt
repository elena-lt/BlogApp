package com.domain.viewState

import com.domain.models.AuthTokenDomain

data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var LoginFields: LoginFields? = LoginFields(),
    var authToken: AuthTokenDomain? = null
)

data class RegistrationFields(

    val registrationEmail: String? = null,
    val registrationUsername: String? = null,
    val registrationPassword: String? = null,
    val registrationConfirmPassword: String? = null

){
    class RegistrationError{
        companion object {
            fun mustFillAllFields(): String {
                return "All fields must are required"
            }
            fun passwordsDoNotMatch(): String {
                return "Passwords do not match"
            }
            fun none(): String {
                return "None"
            }
        }
    }

    fun isValidForRegistration(): String{
        if (registrationEmail.isNullOrEmpty()
            || registrationUsername.isNullOrEmpty()
            || registrationPassword.isNullOrEmpty()
            || registrationConfirmPassword.isNullOrEmpty()
        ) {
            return RegistrationError.mustFillAllFields()
        }

        if (registrationPassword != registrationConfirmPassword) {
            return RegistrationError.passwordsDoNotMatch()
        }

        return RegistrationError.none()
    }
}

data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
) {
    class LoginError {

        companion object {

            fun mustFillAllFields(): String {
                return "You can't login without an email and password."
            }

            fun none(): String {
                return "None"
            }
        }
    }

    fun isValidForLogin(): String {

        if (login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()
        ) {

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }
}
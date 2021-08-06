package com.data.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.data.models.AuthToken
import com.data.persistance.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

    private val _cashedToken = MutableLiveData<AuthToken>()
    val cashedToken: LiveData<AuthToken>
        get() = _cashedToken

    fun login(newValue: AuthToken) {
        setValue(newValue)
    }

    fun logout() {
        Log.d("SESSION_MANAGER", "logout....")

        GlobalScope.launch(IO) {
            var errorMsg: String? = null
            try {
                cashedToken.value!!.account_primary_key?.let {
                    authTokenDao.nullifyToken(it)
                }
            } catch (e: CancellationException) {
                Log.d("SESSION_MANAGER", "logout: ${e.message}")
                errorMsg = e.message
            } catch (e: Exception) {
                Log.d("SESSION_MANAGER", "logout: ${e.message}")
                errorMsg += "\n ${e.message}"
            } finally {
                errorMsg?.let {
                    Log.e("SESSION_MANAGER", "$errorMsg")
                }
                Log.d("SESSION_MANAGER", "Finaly.....")
                setValue(null)
            }
        }
    }

    private fun setValue(newToken: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cashedToken.value != newToken) {
            }
            _cashedToken.value = newToken
        }
    }

    fun isConnectedToInternet(): Boolean {
        val context = application.applicationContext
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}
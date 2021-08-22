package com.data.repository

import android.util.Log
import com.data.models.BlogSearchResponse
import com.data.utils.ApiResult
import com.data.utils.Const.CACHE_TIMEOUT
import com.data.utils.Const.NETWORK_TIMEOUT
import com.data.utils.ErrorHandling.Companion.CACHE_ERROR_TIMEOUT
import com.data.utils.ErrorHandling.Companion.NETWORK_ERROR
import com.data.utils.ErrorHandling.Companion.NETWORK_ERROR_TIMEOUT
import com.data.utils.ErrorHandling.Companion.UNKNOWN_ERROR
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

@ExperimentalCoroutinesApi
abstract class NetworkBoundResource<NetworkObj, CacheObj, ViewState>(
    private val dispatcher: CoroutineDispatcher,
    private val apiCall: (suspend () -> NetworkObj?)? = null,
    val cacheCall: (suspend () -> CacheObj?)? = null
) {
    val result: Flow<DataState<ViewState>> = flow {

        emit(DataState.LOADING(isLoading = true))

        cacheCall?.let {
            emitAll(safeCacheCall())
        }

        apiCall?.let {
            emitAll(safeAPICall())
        }
    }

    private suspend fun safeAPICall() = flow {
            try {
                // throws TimeoutCancellationException
                withTimeout(NETWORK_TIMEOUT) {
                    val result = apiCall?.invoke()
                    if (result == null) {
                        emit(buildDialogError(UNKNOWN_ERROR))
                    } else {
                        updateCache(result)
                        handleNetworkSuccess(result)?.let {
                            emit(it)
                        }
                    }
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                when (throwable) {
                    is TimeoutCancellationException -> {
                        Log.d("AppDebug", "safeAPICall: TimeoutCancellationException ${throwable}")
                        emit(buildDialogError(NETWORK_ERROR_TIMEOUT))
                    }
                    is IOException -> {
                        Log.d("AppDebug", "safeAPICall: IOException ${throwable}")
                        emit(buildDialogError(NETWORK_ERROR))
                    }
                    is HttpException -> {
                        Log.d("AppDebug", "safeAPICall: HttpException ${throwable}")
                        emit(buildDialogError(convertErrorBody(throwable)))
                    }
                    else -> {
                        Log.d("AppDebug", "safeAPICall: Unknown error ${throwable}")
                        emit(buildDialogError(UNKNOWN_ERROR))
                    }
                }
            }
    }.flowOn(Dispatchers.IO)

    private suspend fun safeCacheCall() = flow {
            try {
                // throws TimeoutCancellationException
                withTimeout(CACHE_TIMEOUT) {
                    val result = cacheCall?.invoke()
                    if (result != null) {
                        handleCacheSuccess(result)?.let {
                            emit(it)
                        }
                    }
                }
            } catch (throwable: Throwable) {
                when (throwable) {
                    is TimeoutCancellationException -> {
                        emit(buildDialogError(CACHE_ERROR_TIMEOUT))
                    }
                    else -> {
                        emit(buildDialogError(UNKNOWN_ERROR))
                    }
                }
            }
    }.flowOn(Dispatchers.IO)

    open suspend fun updateCache(networkObject: NetworkObj) {}

    open suspend fun handleNetworkSuccess(response: NetworkObj):
            DataState<ViewState>? {
        return null
    }

    open suspend fun handleCacheSuccess(response: CacheObj): DataState<ViewState>? {
        return null
    }

    private fun convertErrorBody(throwable: HttpException): String? {
        return try {
            throwable.response()?.errorBody()?.string()
        } catch (exception: Exception) {
            UNKNOWN_ERROR
        }
    }

    fun buildDialogError(
        message: String?
    ): DataState<ViewState> {
        return DataState.ERROR(
            StateMessage(
                message = message,
                uiComponentType = UIComponentType.DIALOG,
                messageType = MessageType.ERROR
            )
        )
    }
}


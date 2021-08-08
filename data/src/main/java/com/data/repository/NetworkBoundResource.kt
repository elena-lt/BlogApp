package com.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.data.utils.Const.NETWORK_TIMEOUT
import com.data.utils.Const.TESTING_CACHE_DELAY
import com.data.utils.Const.TESTING_NETWORK_DELAY
import com.data.utils.ErrorHandling
import com.data.utils.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.data.utils.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.data.utils.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.data.utils.GenericApiResponse
import com.domain.utils.DataState
import com.domain.utils.Response
import com.domain.utils.ResponseType
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

@InternalCoroutinesApi
abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean,
    isNetworkRequest: Boolean,
    shouldCancelIfNoInternet: Boolean,
    shouldLoadFromCache: Boolean
) {
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(iniNewJob())
        setValue(DataState.loading(true, cashedData = null))

        if (shouldLoadFromCache) {
            val dbSource = loadFromCache()
            result.addSource(dbSource) {
                result.removeSource(dbSource)
                setValue(DataState.loading(isLoading = true, cashedData = it))
            }
        }

        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                doNetworkRequest()
            } else {
                if (shouldCancelIfNoInternet) {
                    onErrorReturned(
                        UNABLE_TODO_OPERATION_WO_INTERNET,
                        shouldUseDialog = true,
                        shouldUseToast = false
                    )
                } else {
                    doCacheRequest()
                }
            }
        } else {
            doCacheRequest()
        }
    }

    private fun doNetworkRequest() {
        //simulate network delay
        coroutineScope.launch {
//                    delay(TESTING_NETWORK_DELAY)
            withContext(Main) {
                val apiResponse = createCall()
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }
//                GlobalScope.launch(IO) {
//                    delay(NETWORK_TIMEOUT)
//                    if (!job.isCompleted) {
//                        Log.d("AppDebug", "NetworkBoundResource: JOB NETWORK TIMEOUT")
//                        job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
//                    }
//                }
    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            //default = 0
            delay(TESTING_CACHE_DELAY)

            //View data from cache Only
            createCashRequestAndReturn()
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is GenericApiResponse.ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is GenericApiResponse.ApiEmptyResponse -> {
                Log.d("AppDebug", "NetworkBoundResource: returned nothing} ")
            }
            is GenericApiResponse.ApiErrorResponse -> {
                Log.d("AppDebug", "NetworkBoundResource: ${response.errorMessage} ")
                onErrorReturned("HTTP 204", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturned(errorMes: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMes
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (msg == null) {
            msg = "ERROR UNKNOWN"
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    private fun iniNewJob(): Job {
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.d("AppDebug", "NetworkBoundResource: Job has been canceled")
                        cause?.let {
                            onErrorReturned(
                                it.message,
                                shouldUseDialog = false,
                                shouldUseToast = true
                            )
                        } ?: onErrorReturned(
                            "UNKNOWN ERROR",
                            shouldUseDialog = false,
                            shouldUseToast = true
                        )
                    } else if (job.isCompleted) {
                        Log.d("AppDebug", "NetworkBoundResource: Job has been completed")
                        /*no-ops*/
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    //returned when network is down
    abstract suspend fun createCashRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    abstract fun setJob(job: Job)
}
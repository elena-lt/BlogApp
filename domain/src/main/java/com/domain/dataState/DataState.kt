package com.domain.dataState

import com.domain.utils.*

sealed class DataState<T>(
    var loading: Boolean,
    var data: T? = null,
    var stateMessage: StateMessage? = null
) {
    class LOADING<T>(
        isLoading: Boolean,
        cachedData: T? = null
    ) : DataState<T>(
        loading = isLoading,
        data = cachedData
    )

    class SUCCESS<T>(
        data: T? = null,
        stateMessage: StateMessage? = null
    ) : DataState<T>(
        loading = false,
        data = data,
        stateMessage = stateMessage
    )

    class ERROR<T>(
        stateMessage: StateMessage
    ) : DataState<T>(
        loading = false,
        data = null,
        stateMessage = stateMessage
    )
}
package com.domain.utils

data class DataState<T>(
    val error: Event<StateError>? = null,
    val loading: Loading = Loading(false),
    val data: Data<T>? = null
) {

    companion object {
        fun <T> error(
            response: Response
        ): DataState<T> {
            return DataState(error = Event(StateError(response)))
        }

        fun <T> loading(
            isLoading: Boolean,
            cashedData: T? = null
        ): DataState<T> {
            return DataState(
                loading = Loading(isLoading),
                data = Data(
                    Event.dataEvent(cashedData),
                    null
                )
            )
        }

        fun <T> data(
            data: T? = null,
            response: Response? = null
        ): DataState<T> {
            return DataState(
                data = Data(
                    Event.dataEvent(data),
                    Event.responseEvent(response)
                )
            )
        }
    }
}

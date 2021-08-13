package com.blogapp.utils

import com.data.utils.ErrorHandling
object ErrorPaginationDone {
    fun isPaginationDone(errorResponse: String?): Boolean{
        // if error response = '{"detail":"Invalid page."}' then pagination is finished
        return ErrorHandling.PAGINATION_DONE_ERROR.equals(
            ErrorHandling.parseDetailJsonResponse(
                errorResponse
            )
        )
    }
}
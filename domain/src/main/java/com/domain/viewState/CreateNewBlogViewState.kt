package com.domain.viewState

import android.net.Uri

data class CreateNewBlogViewState(
    var newBlogFields: NewBlogFields = NewBlogFields()
) {

    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newBlogImageUri: Uri? = null
    )
}
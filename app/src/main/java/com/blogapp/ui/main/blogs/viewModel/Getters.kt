package com.blogapp.ui.main.blogs.viewModel

import android.net.Uri
import com.domain.models.BlogPostDomain

fun BlogViewModel.getIsQueryInProgress(): Boolean {
    currentState.let {
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getIsQueryExhausted(): Boolean {
    currentState.let {
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.getPage(): Int {
    currentState.let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getSearchQuery(): String {
    currentState.let {
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.getFilter(): String {
    currentState.let {
        return it.blogFields.filter
    }
}

fun BlogViewModel.getOrder(): String {
    currentState.let {
        return it.blogFields.order
    }
}

fun BlogViewModel.getBlogPost(): BlogPostDomain {
    currentState.let {
        it.viewBlogFields.blogPost?.let { blogPost ->
            return blogPost
        } ?: return BlogPostDomain(-1, "", "", "", "", "1", "")
    }
}

fun BlogViewModel.getSlug(): String {
    currentState.let {
        return it.viewBlogFields.blogPost?.let {
            it.slug
        } ?: ""
    }
}

fun BlogViewModel.isAuthorOfBlogPost(): Boolean {
    currentState.let {
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}

fun BlogViewModel.getUpdatedBogUri(): Uri? {
    currentState.let {
        it.updateBlogFields.imageUri?.let {
            return it
        }
    }
    return null
}
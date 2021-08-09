package com.blogapp.models.mappers

import com.blogapp.models.BlogPost
import com.domain.models.BlogPostDomain

object BlogPostMapper {

    fun toBlogPostDomain(blogPost: BlogPost): BlogPostDomain {
        return BlogPostDomain(
            blogPost.primaryKey,
            blogPost.title,
            blogPost.slug,
            blogPost.body,
            blogPost.image,
            blogPost.date_updated,
            blogPost.username
        )
    }

    fun toBlogPost(blogPost: BlogPostDomain): BlogPost {
        return BlogPost(
            blogPost.primaryKey,
            blogPost.title,
            blogPost.slug,
            blogPost.body,
            blogPost.image,
            blogPost.date_updated,
            blogPost.username
        )
    }
}
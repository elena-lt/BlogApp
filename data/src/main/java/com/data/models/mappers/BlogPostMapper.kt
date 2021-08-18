package com.data.models.mappers

import com.data.models.BlogPostEntity
import com.domain.models.BlogPostDomain

object BlogPostMapper {

    fun toBlogPostDomain(bp: BlogPostEntity): BlogPostDomain {
        return BlogPostDomain(
            bp.primaryKey,
            bp.title,
            bp.slug,
            bp.body,
            bp.image,
            bp.date_updated,
            bp.username
        )
    }

    fun toBlogPostData(bp: BlogPostDomain): BlogPostEntity{
        return BlogPostEntity(
            bp.primaryKey,
            bp.title,
            bp.slug,
            bp.body,
            bp.image,
            bp.date_updated,
            bp.username
        )
    }
}
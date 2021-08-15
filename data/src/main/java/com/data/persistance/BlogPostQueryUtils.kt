package com.data.persistance

import androidx.lifecycle.LiveData
import com.data.models.BlogPostEntity
import com.data.persistance.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED
import com.data.persistance.BlogQueryUtils.Companion.ORDER_BY_DESC_DATE_UPDATED

class BlogQueryUtils {


    companion object{

        // values
        const val BLOG_ORDER_ASC: String = ""
        const val BLOG_ORDER_DESC: String = "-"
        const val BLOG_FILTER_DATE_UPDATED = "date_updated"

        val ORDER_BY_ASC_DATE_UPDATED = BLOG_ORDER_ASC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_DESC_DATE_UPDATED = BLOG_ORDER_DESC + BLOG_FILTER_DATE_UPDATED

    }
}


fun BlogPostDao.returnOrderedBlogQuery(
    query: String,
    filterAndOrder: String,
    page: Int
): LiveData<List<BlogPostEntity>> {

    when{

        filterAndOrder.contains(ORDER_BY_DESC_DATE_UPDATED) ->{
            return searchBlogPostsOrderByDateDESC(
                query = query,
                page = page)
        }

        filterAndOrder.contains(ORDER_BY_ASC_DATE_UPDATED) ->{
            return searchBlogPostsOrderByDateASC(
                query = query,
                page = page)
        }

        else ->
            return searchBlogPostsOrderByDateASC(
                query = query,
                page = page
            )
    }
}
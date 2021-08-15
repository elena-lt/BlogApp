package com.domain.utils

object Const {

    // values
    const val BLOG_ORDER_ASC: String = ""
    const val BLOG_ORDER_DESC: String = "-"
    const val BLOG_FILTER_DATE_UPDATED = "date_updated"

    val ORDER_BY_ASC_DATE_UPDATED = BLOG_ORDER_ASC + BLOG_FILTER_DATE_UPDATED
    val ORDER_BY_DESC_DATE_UPDATED = BLOG_ORDER_DESC + BLOG_FILTER_DATE_UPDATED
}
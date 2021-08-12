package com.data.repository.main.blog.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.data.models.BlogPostEntity
import com.data.models.mappers.BlogPostMapper
import com.data.network.main.OpenApiMainService
import com.data.session.SessionManager
import com.data.utils.Const
import com.data.utils.GenericApiResponse
import com.domain.models.BlogPostDomain

class BlogPostsPagingSource(
    private val openApiMainService: OpenApiMainService,
    private val sessionManager: SessionManager
) : PagingSource<Int, BlogPostDomain>() {

    init {
        Log.d("AppDebug", ": BlogPostsPagingSource initiated")
    }

    override fun getRefreshKey(state: PagingState<Int, BlogPostDomain>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BlogPostDomain> {
        Log.d("AppDebug", "Inside load fun")
        val token = sessionManager.cashedToken.value
        val position = params.key ?: Const.STARTING_PAGE_INDEX

        val response =
            openApiMainService.searchBlogPosts(
                authorization = "Token ${token!!.token}",
                page = position
            )
        return if (response.isSuccessful) {
            Log.d("AppDebug", "searchBlogPosts 4reswponse is successfull")
            LoadResult.Page(
                data = response.body()?.results?.map {
                    BlogPostMapper.toBlogPostDomain(it)
                } ?: emptyList(),
                prevKey = if (position == Const.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (response.body()?.results?.size!! < position) null else position + 1
            )
        } else {
            Log.d("AppDebug", "empty response")
            LoadResult.Error(
                response.message() as Throwable
            )
        }
    }
}

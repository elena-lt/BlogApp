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
import retrofit2.HttpException
import java.io.IOException

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
        val token = sessionManager.cashedToken.value
        val position = params.key ?: Const.STARTING_PAGE_INDEX

        return try {
            val response =
                openApiMainService.searchBlogPosts(
                    authorization = "Token ${token!!.token}",
                    page = position
                )
            if (response.isSuccessful) {
                LoadResult.Page(
                    data = response.body()?.results?.map {
                        BlogPostMapper.toBlogPostDomain(it)
                    } ?: emptyList(),
                    prevKey = if (position == Const.STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (response.body()?.results?.size!! < position) null else position + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Log.d("AppDebug", e.message ?: "Unknown IOException")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("AppDebug", e.message ?: "Unknown HttpException ")
            LoadResult.Error(e)
        }
    }
}

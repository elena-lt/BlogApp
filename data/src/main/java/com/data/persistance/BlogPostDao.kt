package com.data.persistance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.data.models.BlogPostEntity
import com.data.utils.Const.PAGINATION_PAGE_SIZE

@Dao
interface BlogPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlogPost(blogPost: BlogPostEntity): Long

//    @Query ("SELECT * FROM blog_post")
//    fun getAllBlogsPosts(): LiveData<List<BlogPostEntity>>

    @Query("""
        SELECT * FROM blog_post
        WHERE title LIKE '%' || :query || '%'
        OR body LIKE '%' || :query || '%'
        OR username LIKE '%' || :query || '%'
        LIMIT (:page * :pageSize)
    """)
    fun getAllBlogsPosts(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): LiveData<List<BlogPostEntity>>
}
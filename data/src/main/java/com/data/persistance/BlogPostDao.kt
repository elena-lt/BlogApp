package com.data.persistance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.data.models.BlogPostEntity

@Dao
interface BlogPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlogPost(blogPost: BlogPostEntity): Long

    @Query ("SELECT * FROM blog_post")
    fun getAllBlogsPosts(): LiveData<List<BlogPostEntity>>
}
package com.data.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.data.models.AccountProperties
import com.data.models.AuthToken
import com.data.models.BlogPostEntity

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPostEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

}
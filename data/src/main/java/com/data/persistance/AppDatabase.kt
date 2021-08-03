package com.data.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.data.models.AccountProperties
import com.data.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

}
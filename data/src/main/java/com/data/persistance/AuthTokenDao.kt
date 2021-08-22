package com.data.persistance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.data.models.AuthToken

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authToken: AuthToken): Long

    @Query ("UPDATE auth_token SET token =null WHERE account_primary_key = :account_primary_key")
    suspend fun nullifyToken(account_primary_key: Int): Int

    @Query ("SELECT * FROM auth_token WHERE account_primary_key = :primary_key")
    suspend fun searchByPrimaryKey(primary_key: Int): AuthToken?

}
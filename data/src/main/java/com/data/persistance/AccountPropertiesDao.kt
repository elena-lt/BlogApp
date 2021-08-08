package com.data.persistance

import androidx.lifecycle.LiveData
import androidx.room.*
import com.data.models.AccountProperties

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE primary_key =:primaryKey")
    fun searchByPrimaryKey(primaryKey: Int): LiveData<AccountProperties>

    @Query("SELECT * FROM account_properties WHERE email =:email")
    fun searchByEmail(email: String): AccountProperties?

    @Query("UPDATE account_properties SET email = :email, username = :username WHERE primary_key = :primaryKey")
    fun updateAccountProperties(primaryKey: Int, email: String, username: String)

}
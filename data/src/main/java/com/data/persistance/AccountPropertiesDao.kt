package com.data.persistance

import androidx.room.*
import com.data.models.AccountProperties

@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnore(accountProperties: AccountProperties): Long

    @Query("SELECT * FROM account_properties WHERE primary_key =:primaryKey")
    fun searchByPrimaryKey(primaryKey: Int): AccountProperties?

    @Query("SELECT * FROM account_properties WHERE email =:email")
    fun searchByEmail(email: String): AccountProperties?

}
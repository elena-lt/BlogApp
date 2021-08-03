package com.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "auth_token",
    foreignKeys = [
        ForeignKey(
            entity = AccountProperties::class,
            parentColumns = ["primary_key"],
            childColumns = ["account_primary_key"],
            onDelete = CASCADE
        )
    ]
)
data class AuthToken(
    @PrimaryKey
    @ColumnInfo(name = "account_primary_key")
    val account_primary_key: Int? = -1,

    @SerializedName("token")
    @Expose
    @ColumnInfo(name = "token")
    val token: String? = ""
)
package com.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "blog_post")
data class BlogPostEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "primary_key")
    @SerializedName("pk")
    @Expose
    var primaryKey: Int,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    var title: String,

    @ColumnInfo(name = "slug")
    @SerializedName("slug")
    @Expose
    var slug: String,

    @ColumnInfo(name = "body")
    @SerializedName("body")
    @Expose
    var body: String,

    @ColumnInfo(name = "image")
    @SerializedName("image")
    @Expose
    var image: String,

    @ColumnInfo(name = "date_updated")
    @SerializedName("date_updated")
    @Expose
    var date_updated: String,

    @ColumnInfo(name = "username")
    @SerializedName("username")
    @Expose
    var username: String
)

data class BlogSearchResponse(
    @SerializedName("results")
    @Expose
    val results: List<BlogPostEntity>,

    @SerializedName("detail")
    @Expose
    val detail: String
)

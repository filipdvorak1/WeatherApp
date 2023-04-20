package com.simple.weather.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavTable")
data class FavouriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "City")
    val city:String,
    @ColumnInfo(name = "Country")
    val country:String ,
    @ColumnInfo(name = "data")
    val data:String ,
    @ColumnInfo(name = "date")
    val date :String ,

    )

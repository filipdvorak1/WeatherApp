package com.simple.weather.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.simple.weather.core.data.entity.FavouriteEntity

@Database(entities = [FavouriteEntity::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

}
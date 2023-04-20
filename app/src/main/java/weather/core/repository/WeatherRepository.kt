package com.simple.weather.core.repository

import android.util.Log

import com.google.gson.Gson
import com.simple.weather.core.data.DataOrException
import com.simple.weather.core.data.WeatherDao
import com.simple.weather.core.data.entity.FavouriteEntity
import com.simple.weather.core.model.WeatherModel
import com.simple.weather.core.network.WeatherApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao
) {

    suspend fun getWeather(city:String) : DataOrException<WeatherModel, Boolean, Exception> {

        val response  = try {
             weatherApi.getWeather(city)
        }
        catch (e:Exception){
            Log.e("EXCEPTION", e.toString())
            return DataOrException(e=e)
        }

        Log.e("RESPONSE", Gson().toJson(response))

        return DataOrException(data = response)

    }


    fun getFavourites() : Flow<List<FavouriteEntity>>  = weatherDao.getFavorites()

    suspend fun getFavouriteByCity(city:String) : FavouriteEntity?  = weatherDao.getFavoriteByCity(city = city)

    suspend fun insertFav(fav:FavouriteEntity)  = weatherDao.insertFav(fav)

    suspend fun updateFav(fav:FavouriteEntity)  = weatherDao.updateFav(fav)


    suspend fun deleteFav(fav:FavouriteEntity)  = weatherDao.removeFav(fav)

    suspend fun deleteAllFavs()  = weatherDao.deleteAllFav( )



}
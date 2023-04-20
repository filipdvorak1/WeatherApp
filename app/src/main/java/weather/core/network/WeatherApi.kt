package com.simple.weather.core.network

import com.simple.weather.core.model.WeatherModel
import com.simple.weather.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface WeatherApi {

    @GET(value = "data/2.5/forecast?")
    suspend fun getWeather(
        @Query("q") query: String,
        @Query("units") units:String = "imperial",
        @Query("appid") appid: String = Constants.API_KEY,
    ) : WeatherModel

}
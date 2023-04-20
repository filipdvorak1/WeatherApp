package com.simple.weather.core.model
import com.google.gson.annotations.SerializedName
import com.simple.weather.core.model.City
import com.simple.weather.core.model.DataList

data class WeatherModel(
    @SerializedName("city")
    var city: City?,
    @SerializedName("cnt")
    var cnt: Int?,
    @SerializedName("cod")
    var cod: String?,
    @SerializedName("list")
    var list: List<DataList?>?,
    @SerializedName("message")
    var message: Int?
)
package com.simple.weather.core.model
import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("lon")
    var lon: Double?
)
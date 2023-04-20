package com.simple.weather.core.model
import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    var h: Double?
)
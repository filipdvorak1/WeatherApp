package com.simple.weather.core

data class UserAddress(
    var city : String = "",
    var state : String = "",
    var country : String = "",
    val lan : Double = 0.0,
    var lon: Double = 0.0
)

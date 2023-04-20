package com.simple.weather.utils

import java.text.SimpleDateFormat
import java.util.*


fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("EEE, MMM d")
    val date = Date(this * 1000)
    return sdf.format(date)
}

fun Long.formatDatetime(): String {
    val sdf = SimpleDateFormat("hh:mm:aa")
    val date = Date(this * 1000)
    return sdf.format(date)
}

fun Double.formatDecimals(): String {
    return " %.0f".format(this)
}

fun Long.LastupdateformatDatetime(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy h:mm a")
    val date = Date(this * 1000)
    return sdf.format(date)
}

package com.simple.weather.ui.screens

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simple.weather.R
import com.simple.weather.core.model.City
import com.simple.weather.core.model.WeatherModel
import com.simple.weather.utils.formatDatetime
import com.simple.weather.utils.formatDecimals
import com.simple.weather.viewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Date

@Composable
fun LocationScreen(modifier: Modifier, viewmodel: viewmodel) {
    var value by remember {
        mutableStateOf("")
    }
    var loading by remember {
        mutableStateOf(false)
    }


    val cityWeather by viewmodel.cityWeather.collectAsState()

    LaunchedEffect(key1 = cityWeather) {

        if (cityWeather != null) {
            loading = false
        }

    }

    var scope = rememberCoroutineScope()

    var searchCity : (String) -> Unit = {


        if (it.length > 3) {

            scope.launch(Dispatchers.IO) {
                viewmodel.searchCity(it)

                loading= true
            }

        }


    }


    // Top Screen
            Column(modifier.padding(start = 16.dp , end = 16.dp)) {


                Spacer(modifier = Modifier.size(8.dp))

        Text(text = "Enter any city name : ")
                Spacer(modifier = Modifier.size(8.dp))



        Box(Modifier.fillMaxWidth(), Alignment.Center) {

            OutlinedTextField(
                value = value,
                onValueChange = { newText ->
                    value = newText
                },
                label = { Text(text = "City Name") },
                placeholder = { Text(text = "Eg: London") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )
        }

                Spacer(modifier = Modifier.size(8.dp))

        Box(Modifier.fillMaxWidth(), Alignment.Center) {

            Button(onClick = {

                searchCity(value)

            }) {
                Text(text = "Get weather")
            }
        }

                Spacer(modifier = Modifier.size(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)) {


                    if (loading) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator()
                        }

                    } else {

                        if (cityWeather != null) {

                            LocationDetails(cityWeather!!){
                                scope.launch(Dispatchers.IO) {
                                    viewmodel.addFav()
                                }


                            }
                        }
                    }



                }


    }


}



@Composable
fun LocationDetails(cityWeatherStatus: Pair<WeatherModel, Boolean> , onClick : () -> Unit) {

    val cityWeather: WeatherModel =  cityWeatherStatus.first

    val weatherItem = cityWeather.list?.get(0)
    val imgIconUrl = "https://openweathermap.org/img/wn/${weatherItem?.weather?.get(0)?.icon}.png"


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    WeatherStateImage(imageUrl = imgIconUrl)
                    weatherItem?.weather?.get(0)?.description?.let {
                        Text(
                            text = it,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    weatherItem?.main?.temp?.let { fahrenheitTemp ->
                        val celsiusTemp = ((fahrenheitTemp - 32) * 5) / 9
                        Text(
                            text = celsiusTemp.formatDecimals() + "°C",
                            style = MaterialTheme.typography.h5
                        )
                    }
                    weatherItem?.weather?.get(0)?.main?.let {
                        Text(
                            text = it,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${cityWeather.city?.name ?: "-"} ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${cityWeather.city?.country ?: "-"}")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.sunrise),
                        contentDescription = "sunrise icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${cityWeather.city?.sunrise?.toLong()?.formatDatetime()}",
                        style = MaterialTheme.typography.caption
                    )
                }
                Column(modifier = Modifier.padding(4.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.sunset),
                        contentDescription = "sunset icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${cityWeather.city?.sunset?.toLong()?.formatDatetime()}",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            if (weatherItem != null) {
                WeatherRowHumidity(weather = weatherItem)
            }

            if (!cityWeatherStatus.second) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            onClick.invoke()

                        },

                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Add to Favourites")
                    }
                }
            }


        }
    }




//    Card {
//
//        Column() {
//
//
//            Row(Modifier.fillMaxWidth()) {
//
//
//                Column() {
//                    WeatherStateImage(imageUrl = imgIconUrl)
//                    weatherItem?.weather?.get(0)?.description?.let {
//                        androidx.compose.material.Text(
//                            text = it,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp
//                        )
//                    }
//                }
//                Column() {
//                    weatherItem?.main?.temp?.let {
//                        androidx.compose.material.Text(
//                            text = it.formatDecimals() + "°",
//                            style = MaterialTheme.typography.h5
//                        )
//                    }
//                    weatherItem?.weather?.get(0)?.main?.let {
//                        androidx.compose.material.Text(
//                            text = it,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 14.sp
//                        )
//                    }
//
//                }
//
//                Column() {
//                    Text(text = "${cityWeather.city?.name ?: "-"} ")
//                    Text(text = "${cityWeather.city?.country ?: "-"}")
//
//
//                }
//            }
//
//            Row(Modifier.fillMaxWidth()) {
//
//                Column(modifier = Modifier.padding(4.dp)) {
//
//                    Icon(
//                        painter = painterResource(id = R.drawable.sunrise),
//                        contentDescription = "sunrise icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//
//                    androidx.compose.material.Text(
//                        text = "${cityWeather.city?.sunrise?.toLong()?.formatDatetime()}",
//                        style = MaterialTheme.typography.caption
//                    )
//
//
//                }
//
//                Column(modifier = Modifier.padding(4.dp)) {
//
//                    Icon(
//                        painter = painterResource(id = R.drawable.sunset),
//                        contentDescription = "sunset icon",
//                        modifier = Modifier.size(20.dp)
//                    )
//
//                    androidx.compose.material.Text(
//                        text = "${cityWeather.city?.sunset?.toLong()?.formatDatetime()}",
//                        style = MaterialTheme.typography.caption
//                    )
//
//                }
//
//
//            }
//
//            if (weatherItem != null) {
//                WeatherRowHumidity(weather = weatherItem)
//            }
////
//
//
//            Box(Modifier.fillMaxWidth()) {
//
//
//                Button(onClick = { /*TODO*/ }) {
//                    Text(text = "Add to Favourite")
//                }
//
//            }
//
//        }
//
//
//    }


}

@Composable
fun CityDetails(city: City) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = "${city.name ?: "-"} , ${city.country ?: "-"}")
        Text(text = "Coordinates:")
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Latitude: ${city.coord?.lat ?: "-"}")
            Text(text = "Longitude: ${city.coord?.lon ?: "-"}")
        }
        Text(text = "Sunrise time: ${city.sunrise?.let { formatTime(it) } ?: "-"}")
        Text(text = "Sunset time: ${city.sunset?.let { formatTime(it) } ?: "-"}")
    }
}

@SuppressLint("SimpleDateFormat")
private fun formatTime(timestamp: Int): String {
    val sdf = SimpleDateFormat("hh:mm a")
    val date = Date(timestamp * 1000L)
    return sdf.format(date)
}

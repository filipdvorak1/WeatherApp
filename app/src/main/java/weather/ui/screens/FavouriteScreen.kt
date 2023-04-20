package com.simple.weather.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.simple.weather.R
import com.simple.weather.core.data.entity.FavouriteEntity
import com.simple.weather.core.model.WeatherModel
import com.simple.weather.utils.formatDatetime
import com.simple.weather.utils.formatDecimals
import com.simple.weather.viewmodel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun FavouriteScreen(modifier: Modifier, viewmodel: viewmodel) {

    val list = viewmodel.getFavList().collectAsState(initial = emptyList())

    var scope = rememberCoroutineScope()

    // Top Screen
    Column(modifier) {

        Spacer(modifier = Modifier.size(8.dp))

        Text(text = "List of Favourite" , modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)) {


            if (list.value.isEmpty()) {

                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                    Text(text = "No Favourite")
                }


            } else {

                LazyColumn(
                    modifier = Modifier.padding(2.dp), contentPadding = PaddingValues(1.dp)
                ) {

                    items(items = list.value) { weatherItem: FavouriteEntity ->

                        if (weatherItem != null) {
                            FavouriteEntityList(weatherItem) {

                                scope.launch(Dispatchers.IO) {
                                    viewmodel.updateFav(weatherItem)

                                }



                            }
                        }

                    }
                }

            }


        }


    }


}







@Composable
fun FavouriteEntityList(cityWeatherStatus: FavouriteEntity, onClick: () -> Unit) {




    val gson = Gson()
    // Convert the JSON string back to a data class
    val cityWeather : WeatherModel = gson.fromJson( cityWeatherStatus.data, WeatherModel::class.java)


    val weatherItem = cityWeather.list?.get(0)
    val imgIconUrl = "https://openweathermap.org/img/wn/${weatherItem?.weather?.get(0)?.icon}.png"


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {

            }


        ,
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

            val sdf = SimpleDateFormat("MMM dd, yyyy h:mm a")
            val date = Date(cityWeatherStatus.date.toLong())
            val formattedDate = sdf.format(date)
            Text(text = "Last Update : ${formattedDate}" , fontSize = 12.sp)


        }
    }

}
package com.simple.weather.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.simple.weather.R
import com.simple.weather.core.UserAddress
import com.simple.weather.core.data.DataOrException
import com.simple.weather.core.model.City
import com.simple.weather.core.model.DataList
import com.simple.weather.core.model.WeatherModel
import com.simple.weather.ui.dialog.LocationDialog
import com.simple.weather.utils.formatDate
import com.simple.weather.utils.formatDatetime
import com.simple.weather.utils.formatDecimals
import com.simple.weather.viewmodel
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(modifier : Modifier ,viewmodel: viewmodel, callLocation: () -> Unit) {


    // Ask Permission
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    if (locationPermissionsState.allPermissionsGranted) {

        LaunchedEffect(key1 = Unit) {
            delay(300)
            callLocation()
        }


    }


    LaunchedEffect(key1 = Unit) {

        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }

    }


    val locationManager = LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = remember { mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) }

    if (!isLocationEnabled.value) {
        val locationSettingsLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isLocationEnabled.value = true
            }
        }


        LocationDialog(true) {

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            locationSettingsLauncher.launch(intent)
        }

    }

    val address by viewmodel.address.collectAsState()

    val liveWeather by viewmodel.liveWeather.collectAsState()






    var loading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = address.city ) {

       if( address.city.length>3 )
       {
           loading = true
           viewmodel.getLiveWeather(address.city)
       }

    }

    LaunchedEffect(key1 = liveWeather) {

        if (liveWeather != null) {
            loading = false
        }

    }



    // Top Screen
    Column(modifier) {



        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp), contentAlignment = Alignment.Center
        ) {

            DashboardHeading(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(start = 24.dp), address
            )

        }


        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)) {

            if (loading) {

Box(Modifier.fillMaxSize(), Alignment.Center) {
    CircularProgressIndicator()
}

            } else {
//                Text(text = "${liveWeather?.city}")
                if (liveWeather != null) {

                    MainContent(liveWeather!!)
                }
            }


        }



    }


}




@Composable
fun MainContent(weatherData: WeatherModel) {

    val weatherItem = weatherData.list?.get(0)
    val imgIconUrl = "https://openweathermap.org/img/wn/${weatherItem?.weather?.get(0)?.icon}.png"

    Column(
        Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)

        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(Modifier.fillMaxWidth()) {
            weatherItem?.dt?.toLong()?.let {
                Text(
                    text = it.formatDate(),
                    color = androidx.compose.material.MaterialTheme.colors.onSecondary,
                    style = androidx.compose.material.MaterialTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(6.dp),
                    textAlign = TextAlign.Start
                )
            }
        }

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {

            Column(modifier = Modifier.padding(4.dp)) {

                Icon(
                    painter = painterResource(id = R.drawable.sunrise),
                    contentDescription = "sunrise icon",
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = "${weatherData.city?.sunrise?.toLong()?.formatDatetime()}",
                    style = androidx.compose.material.MaterialTheme.typography.caption
                )


            }
            Surface(
                Modifier
                    .padding(4.dp)
                    .size(150.dp), shape = CircleShape, color = Color(0xB3FFC400)
            ) {


                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    WeatherStateImage(imageUrl = imgIconUrl)
                    weatherItem?.weather?.get(0)?.description?.let {
                        Text(
                            text = it,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp
                        )
                    }
                    weatherItem?.main?.temp?.let { fahrenheitTemp ->
                        val celsiusTemp = ((fahrenheitTemp - 32) * 5) / 9
                        Text(
                            text = celsiusTemp.formatDecimals() + "°C",
                            style = androidx.compose.material.MaterialTheme.typography.h5
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

            }


            Column(modifier = Modifier.padding(4.dp)) {

                Icon(
                    painter = painterResource(id = R.drawable.sunset),
                    contentDescription = "sunset icon",
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = "${weatherData.city?.sunset?.toLong()?.formatDatetime()}",
                    style = androidx.compose.material.MaterialTheme.typography.caption
                )

            }
        }



        if (weatherItem != null) {
            WeatherRowHumidity(weather = weatherItem)
//            Divider()
//            weatherData.city?.let { WeatherRowSunset(cityData = it) }
        }

        Text(
            text = "Recent",
            style = androidx.compose.material.MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = Color.Transparent,
            shape = RoundedCornerShape(size = 14.dp)
        ) {

            LazyColumn(
                modifier = Modifier.padding(2.dp), contentPadding = PaddingValues(1.dp)
            ) {


                items(items = weatherData.list!!) { weatherItem: DataList? ->

                    if (weatherItem != null) {
                        WeatherDetailRow(weatherItem)
                    }

                }
            }

        }

    }
}

@Composable
fun WeatherRowSunset(cityData: City) {

    Row(
        Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(modifier = Modifier.padding(4.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.sunrise),
                contentDescription = "sunrise icon",
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "${cityData.sunrise?.toLong()?.formatDatetime()}",
                style = androidx.compose.material.MaterialTheme.typography.caption
            )


        }



        Row(modifier = Modifier.padding(4.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.sunset),
                contentDescription = "sunset icon",
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "${cityData.sunset?.toLong()?.formatDatetime()}",
                style = androidx.compose.material.MaterialTheme.typography.caption
            )

        }

    }

}

@Composable
fun WeatherDetailRow(weatherData: DataList) {

    val imgIconUrl = "https://openweathermap.org/img/wn/${weatherData.weather?.get(0)?.icon}.png"


    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),

        shape = RoundedCornerShape(8.dp),

    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                weatherData.dt?.toLong()?.let {
                    Text(text = it.formatDate().split(",")[0], modifier = Modifier.padding(5.dp), style = TextStyle(
                        fontSize = 16.sp
                    )
                    )
                    Text(text = it.formatDatetime().split(",")[0], modifier = Modifier.padding(5.dp),style = TextStyle(
                        fontSize = 14.sp
                    )
                    )

                }
            }


            WeatherStateImage(imageUrl = imgIconUrl)

            Surface(
                shape = CircleShape,
                color = Color.Blue.copy(alpha = .3f)
            )
            {

                weatherData.weather?.get(0)?.description?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(4.dp),
                        style = androidx.compose.material.MaterialTheme.typography.caption
                    )
                }

            }

            Text(text = buildAnnotatedString
            {

                withStyle(
                    style = SpanStyle(
                        color = Color.Blue.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                {
                    weatherData.main?.tempMax?.let { fahrenheitTempMax ->
                        val celsiusTempMax = ((fahrenheitTempMax - 32) * 5) / 9
                        celsiusTempMax.formatDecimals()?.let { append("$it°C") }
                    }!!
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                {
                    weatherData.main?.tempMin?.let { fahrenheitTempMin ->
                        val celsiusTempMin = ((fahrenheitTempMin - 32) * 5) / 9
                        celsiusTempMin.formatDecimals()?.let { append("$it°C") }
                    }
                }


            })

        }

    }

}


@Composable
fun WeatherStateImage(imageUrl: String) {

    Image(
        modifier = Modifier.size(60.dp), painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                .transformations(CircleCropTransformation()).crossfade(true)
                .diskCacheKey("data_image_${imageUrl}").networkCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.DISABLED).memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
        ), alignment = Alignment.BottomStart, contentDescription = "image"
    )

}





@Composable
fun WeatherRowHumidity(weather: DataList) {

    Row(
        Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(modifier = Modifier.padding(4.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.humidity),
                contentDescription = "humidity icon",
                modifier = Modifier.size(20.dp)
            )

            Text(text = "${weather.main?.humidity}%", style = androidx.compose.material.MaterialTheme.typography.caption)


        }
        Row(modifier = Modifier.padding(4.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.pressure),
                contentDescription = "pressure icon",
                modifier = Modifier.size(20.dp)
            )

            Text(text = "${weather.main?.pressure} psi", style = androidx.compose.material.MaterialTheme.typography.caption)

        }
        Row(modifier = Modifier.padding(4.dp)) {

            Icon(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "wind icon",
                modifier = Modifier.size(20.dp)
            )

            Text(text = "${weather.wind?.speed} mph", style = androidx.compose.material.MaterialTheme.typography.caption)

        }

    }

}





@Composable
fun DashboardHeading(modifier: Modifier, address: UserAddress?) {

    Column(modifier) {


        if (address != null) {

            val Address = "${address.city},${address.state},${address.country}"
            val updatedAddress = if (Address.length < 5) {
                "Location Unknown"
            } else Address


            androidx.compose.material3.Text(
                text = updatedAddress, style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 8.dp)
            )

        } else {
            androidx.compose.material3.Text(
                text = "Searching...", style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }

        androidx.compose.material3.Text(
            text = "  Live Weather Update ", style = MaterialTheme.typography.labelLarge.copy(
                color = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

    }


}
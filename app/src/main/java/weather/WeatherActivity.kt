package com.simple.weather

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.simple.weather.ui.screens.SplashScreen
import com.simple.weather.ui.theme.SimpleweatherTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {

    companion object {
        private const val TAG = "WeatherActivity"
    }

    @Inject
    lateinit var viewmodel: viewmodel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val callLocation: ()-> Unit =  {

            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            viewmodel.locationProvider(fusedLocationClient)

        }

        setContent {
            SimpleweatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Set the system bars appearance
                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setStatusBarColor(
                            color = Color.White,
                            darkIcons = true
                        )
                        systemUiController.setNavigationBarColor(
                            color = Color.White,
                            darkIcons = true
                        )
                    }

                    val cyurrent = LocalContext.current
                    AppNavigationWithBottomSheet(viewmodel , callLocation)
                }
            }
        }
    }


}
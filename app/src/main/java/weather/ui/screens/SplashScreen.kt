package com.simple.weather.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.simple.weather.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(redrirect: () -> Unit) {

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White ,
            darkIcons = true
        )
    }

    LaunchedEffect(key1 = Unit) {
        delay(1000)
        redrirect.invoke()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White) ,
        contentAlignment = Alignment.Center
    ) {

        Image(painter = painterResource(id = R.drawable.ic_wheather_icon), contentDescription = "", modifier = Modifier
            .width(200.dp)
            .wrapContentHeight())


    }

}
package com.simple.weather.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun LocationDialog(showDialogConnect: Boolean, onDialogChange: (Boolean) -> Unit) {

    var showDialog by remember { mutableStateOf(true) }

    if (!showDialogConnect) {
        showDialog = false

    }

    Dialog(
        onDismissRequest = { showDialog = false },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {


        Box(
            Modifier
                .height(300.dp)
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Location Disabled ",
                        Modifier.padding(bottom = 16.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge.copy()
                    )
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = "Check that your device's location is enabled . so that we can provide accurate Air Quality Information",
                        Modifier.padding(bottom = 16.dp),
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium.copy()
                    )
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Button(
                        onClick = {
                            onDialogChange(false)
                        }
                    ) {
                        androidx.compose.material3.Text(text = "Enable Location")
                    }
                }


            }


        }


    }


}
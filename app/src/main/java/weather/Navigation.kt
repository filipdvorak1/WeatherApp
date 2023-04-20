package com.simple.weather


import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.simple.weather.ui.dialog.LocationDialog
import com.simple.weather.ui.screens.DashboardScreen
import com.simple.weather.ui.screens.FavouriteScreen
import com.simple.weather.ui.screens.LocationScreen
import com.simple.weather.ui.theme.ScreenBackground
import com.simple.weather.ui.theme.TabNotSelection
import com.simple.weather.ui.theme.TabSelection


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AppNavigationWithBottomSheet(viewmodel: viewmodel, callLocation: () -> Unit) {

    // Permission
    val permissionsList: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissionsList
    )

    var isGranted by remember { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current

    val current = LocalContext.current


    // Check where the Location is working is on
    val locationManager = current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = remember { mutableStateOf(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) }


    DisposableEffect(key1 = lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionsState.launchMultiplePermissionRequest()

                val allPermision: Boolean = permissionsState.permissions.all { permis ->
                    permis.status.isGranted
                }
                if (allPermision) {
                    isGranted = true

//                    Toast.makeText(current, " Success Permission !!  ", Toast.LENGTH_SHORT).show()

                   callLocation.invoke()

                } else {

                    val permission: List<String> = permissionsState.permissions.map {
                        if (!it.status.isGranted) {
                            it.permission
                        } else null
                    }.filterNotNull()

/*                    Toast.makeText(
                        current,
                        " Give Permission !! ${permission.toString()} ",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }
            }

        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(key1 = permissionsState) {


        val Allpermision: Boolean = permissionsState.permissions.all { permis ->
            permis.status.isGranted
        }

        val permissions = permissionsState.permissions.map { permis ->
            permis.permission to permis.status.isGranted
        }


        var size = permissions.size

        if (Allpermision) {
            System.out.println(" Shan Permission Granted ")

            callLocation.invoke()

        }

    }


    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White,
            darkIcons = true
        )
    }


    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(Screen.Home, Screen.location, Screen.favourite)
    // App Title


    val title = when (currentRoute) {
        Screen.Home.route -> "Home"
        Screen.favourite.route -> "Favourite"
        Screen.location.route -> "Location"

        else -> "user's Home"
    }

    val alignment = when (currentRoute) {
        Screen.Home.route -> Alignment.CenterStart

        else -> Alignment.Center
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        alignment
                    ) {

                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,

                            )
                    }
                }

            )
        },
        bottomBar = {
//                bottomBar()
            BottomNavigation(
                modifier = Modifier,
                backgroundColor = Color(0xFFFFFFFF),
                contentColor = Color(0xFF000000)
            ) {


                items.forEach { screen ->

                    val isSelected = (currentRoute == screen.route)

                    val Color = if (isSelected) TabSelection else TabNotSelection


                    BottomNavigationItem(
                        icon = {

                            Image(
                                painter = painterResource(id = screen.icon),
                                contentDescription = "",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(Color)
                            )


                        },
                        label = {
                            Text(text = screen.title, color = Color, style = MaterialTheme.typography.titleSmall)


                        },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        })
                }
            }
        }


    ) { innerPadding ->

        NavigationHost(Modifier.fillMaxSize().background(ScreenBackground).padding(innerPadding),navController, viewmodel, callLocation)
    }


    if (!isLocationEnabled.value) {
        val locationSettingsLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                isLocationEnabled.value = true
            }
        }


        LocationDialog(true) {

            val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            locationSettingsLauncher.launch(intent)


        }


    } else {
        // Use the isLocationEnabled state variable to determine if location is enabled or not
    }


}


@Composable
fun topTitle() {

    Text(text = "title")


}


@Composable
fun NavigationHost(modifier: Modifier,navController: NavController, viewmodel: viewmodel, callLocation: () -> Unit) {

    val current = LocalContext.current



    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.Home.route,

        ) {

        composable(Screen.favourite.route) {
            FavouriteScreen(modifier , viewmodel)
        }

        composable(Screen.Home.route) {
            DashboardScreen(modifier,viewmodel , callLocation)
        }

        composable(Screen.location.route) {
            LocationScreen(modifier, viewmodel)
        }

    }


}


sealed class Screen(val route: String, val title: String, val icon: Int) {

    object Home : Screen(
        route = "home",
        title = "Home",
        icon = R.drawable.ic_menu_cloudy

    )

    object location : Screen(
        route = "location",
        title = "Location",
        icon = R.drawable.ic_menu_location
    )

    object favourite : Screen(
        route = "favourite",
        title = "Favourite",
        icon = R.drawable.ic_menu_favourite
    )

}


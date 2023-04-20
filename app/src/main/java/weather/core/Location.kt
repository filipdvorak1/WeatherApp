package com.simple.weather.core

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine

class Location(val context: Context) {

    companion object {
        private const val TAG = "getLocation"
    }

    var _address = MutableStateFlow<UserAddress>(UserAddress())
//    var _Apidetails = MutableStateFlow<ApiResponse?>(null)


    @SuppressLint("MissingPermission")
    suspend fun getLocationMain(fusedLocationClient: FusedLocationProviderClient) {

        // First Step
        val location = getLocation(fusedLocationClient) ?: return

        // Update Results
        _address.update { it.copy(lan = location.latitude, lon = location.longitude) }

        // Call Api
        // todo get weather
      //  callApi(location.latitude, location.longitude)

        // Latitude
        val result: Address? = getAddressFromLocation(context, location.latitude, location.longitude)
        if (result != null) {
            // update
            getUserLocation(result)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    suspend fun getLocation(fusedLocationClient: FusedLocationProviderClient): Location? {

        return suspendCancellableCoroutine { continuation ->

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

                if (location != null) {
                    continuation.resume(location, null)
                } else {
                    continuation.resume(null, null)
                }

            }

        }

    }


/*    suspend fun callApi(latitude: Double, longitude: Double) {

        val apiManager = APIManager(context)

        val result: ApiResponse = apiManager.call(latitude, longitude).first()

        _Apidetails.update { result }


    }*/


    fun getUserLocation(reesult: Address) {

        val city = reesult.locality
        val state = reesult.adminArea
        val country = reesult.countryName

        _address.update { it.copy(city, state, country) }


    }


    fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        if (addresses != null && addresses.isNotEmpty()) {
            return addresses[0]
        }
        return null
    }


}

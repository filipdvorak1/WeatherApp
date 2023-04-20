package com.simple.weather

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.simple.weather.core.Location
import com.simple.weather.core.UserAddress
import com.simple.weather.core.data.DataOrException
import com.simple.weather.core.data.entity.FavouriteEntity
import com.simple.weather.core.model.WeatherModel
import com.simple.weather.core.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class viewmodel @Inject constructor(
    @ApplicationContext val context: Context,
    private val weatherRepository: WeatherRepository

) : ViewModel() {

    companion object {
        private const val TAG = "viewmodel"
    }


    var _liveWeather = MutableStateFlow<WeatherModel?>(null)

        val liveWeather: MutableStateFlow<WeatherModel?>
        get() = _liveWeather

    var _loading = MutableStateFlow<Boolean>(false)

    val loading : MutableStateFlow<Boolean>
        get() = _loading



    var _cityWeather = MutableStateFlow<Pair<WeatherModel,Boolean>?>(null)

    val cityWeather: MutableStateFlow<Pair<WeatherModel,Boolean>?>
        get() = _cityWeather

    val location = Location(context)

    val address: MutableStateFlow<UserAddress>
        get() = location._address


    fun locationProvider(fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "locationProvider() called with: fusedLocationClient = $fusedLocationClient")
        viewModelScope.launch {
            location.getLocationMain(fusedLocationClient)
        }
    }

    suspend fun updateFav(weatherItem: FavouriteEntity) {
        val value = getWeather(weatherItem.city)
        if (value.data != null) {
            val weatherModel = value.data!!
            val gson = Gson()
            val json = withContext(Dispatchers.Default) { gson.toJson(weatherModel) }
            val updateValue = FavouriteEntity(
                city = weatherModel.city?.name ?: "unknowncity",
                country = weatherModel.city?.country ?: "unknowncountry",
                data = json,
                date = System.currentTimeMillis().toString()
            )
            withContext(Dispatchers.IO) {
                weatherRepository.updateFav(updateValue)
            }
        }
    }

//    suspend fun updateFav(weatherItem: FavouriteEntity) {
//
//        val value = getWeather(weatherItem.city)
//        if (value.data != null) {
//            val WeatherModel = value.data!!
//
//            val gson = Gson()
//            val json = gson.toJson(WeatherModel)
//
//            val updatevalue = FavouriteEntity(
//                city = WeatherModel.city?.name ?: "unknowncity",
//                country = WeatherModel.city?.country ?: "unknowncity",
//                data = json ,
//                date = System.currentTimeMillis().toString()
//
//            )
//
//
//
//            weatherRepository.updateFav(updatevalue)
//        }
//
//    }

    suspend fun getLiveWeather(city: String) {

        val value = getWeather(city)


        if (value.data != null) {
            _liveWeather.value =  value.data as WeatherModel
        }


    }

    suspend fun searchCity(city: String) {

        val value = getWeather(city)


        if (value.data != null) {
            _cityWeather.value =  value.data as WeatherModel to false
        }


    }




    fun getFavList(): Flow<List<FavouriteEntity>> {
        return weatherRepository.getFavourites()
    }



    suspend fun addFav(){

        if (_cityWeather.value != null) {
            _cityWeather.value =  _cityWeather.value!!.copy(second = true)
        } else return


       val WeatherModel = _cityWeather.value!!.first

        val gson = Gson()
        val json = gson.toJson(WeatherModel)

        val value = FavouriteEntity(
            city = WeatherModel.city?.name ?: "unknowncity",
            country = WeatherModel.city?.country ?: "unknowncity",
            data = json ,
            date = System.currentTimeMillis().toString()


        )



        weatherRepository.insertFav(value)

    }


    suspend fun getWeather(city:String) : DataOrException<WeatherModel, Boolean, Exception> {
        return weatherRepository.getWeather(city = city)

    }


}
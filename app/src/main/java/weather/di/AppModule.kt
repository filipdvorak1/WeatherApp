package com.simple.weather.di

import android.content.Context
import androidx.room.Room
import com.simple.weather.core.data.WeatherDao
import com.simple.weather.core.data.WeatherDatabase
import com.simple.weather.core.network.WeatherApi
import com.simple.weather.core.repository.WeatherRepository
import com.simple.weather.utils.Constants
import com.simple.weather.viewmodel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

     @Singleton
    @Provides
    fun provideViewmodel(@ApplicationContext context : Context ,    weatherRepository: WeatherRepository) : viewmodel {
        return viewmodel(context,weatherRepository)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }


    @Singleton
    @Provides
    fun provideDao(db: WeatherDatabase) : WeatherDao {
        return db.weatherDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext con: Context) : WeatherDatabase{
        return Room.databaseBuilder(con,WeatherDatabase::class.java,"WeatherDb").fallbackToDestructiveMigration().build()
    }


}
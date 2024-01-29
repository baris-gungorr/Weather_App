package com.barisgungorr.service


import com.barisgungorr.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Response

interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("APPID") appid: String
    ): Response<WeatherModel>

    @GET("weather")
    suspend fun getCityWeatherData(
        @Query("q") q: String,
        @Query("APPID") appid: String
    ): Response<WeatherModel>
}
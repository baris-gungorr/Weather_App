package com.barisgungorr.service


import com.barisgungorr.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface WeatherApi {

    @GET("weather")
    fun getCurrentWeatherData(
        @Query("lat") lat:String,
        @Query("lon") lon: String,
        @Query("APPID") appid:String

    ): Call<WeatherModel>

    @GET("weather")
    fun getCityWeatherData(
        @Query("q") q:String,
        @Query("APPID") appid: String
    ):Call<WeatherModel>

}
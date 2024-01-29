package com.barisgungorr.service

import com.barisgungorr.service.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtil {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiInterface: WeatherApi by lazy {
        retrofit.create(WeatherApi::class.java)
    }
}
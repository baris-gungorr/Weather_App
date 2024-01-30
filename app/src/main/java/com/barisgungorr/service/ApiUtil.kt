package com.barisgungorr.service

import com.barisgungorr.service.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtil {

    private var retrofit: Retrofit? = null
    fun apiInterface(): WeatherApi? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofit?.create(WeatherApi::class.java)
    }
}
package com.example.deltaapp2.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://task2.deltaforce.club/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: AstroApi = retrofit.create(AstroApi::class.java)
}
package com.example.deltaapp2.data

import com.example.deltaapp2.Models.AstroSkins
import com.example.deltaapp2.Models.AuthReq
import com.example.deltaapp2.Models.AuthResponse
import com.example.deltaapp2.Models.UIcolor
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AstroApi{
    @GET ("api/color")
    suspend fun getColor(): UIcolor
    @GET ("api/skins")
    suspend fun getSkins(): AstroSkins
    @POST("api/login")
    suspend fun doLogin(@Body request: AuthReq): AuthResponse
    @POST("api/register")
    suspend fun doRegister(@Body request: AuthReq): AuthResponse
    //User wants an AuthResponse, so I'll open the package, parse JSON, and create the object.
    @GET("api/themePack/image")
    suspend fun getBg(): Response<ResponseBody> // understand difference bw ResponseBody and Response<ResponseBody> = status code + headers + raw body
//responsebody is retrofit way of telling raw response with no gson conversion to data class
    @GET("api/themePack/audio")
    suspend fun getAudio(): Response<ResponseBody>
}
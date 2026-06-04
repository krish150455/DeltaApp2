package com.example.deltaapp2.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.deltaapp2.Models.AstroSkins
import com.example.deltaapp2.Models.AuthReq
import com.example.deltaapp2.Models.AuthResponse
import com.example.deltaapp2.Models.UIcolor
import okhttp3.ResponseBody
import retrofit2.Response

class AstroRepository {
    suspend fun getColor(): UIcolor {
        return RetrofitInstance.api.getColor()
    }
    suspend fun getSkins(): AstroSkins{
        return RetrofitInstance.api.getSkins()
    }
    suspend fun doLogin(request: AuthReq): AuthResponse{
        return RetrofitInstance.api.doLogin(request)
    }
    suspend fun doRegister(request: AuthReq): AuthResponse{
        return RetrofitInstance.api.doRegister(request)
    }
    suspend fun getBg(): Bitmap?{
        val response = RetrofitInstance.api.getBg()
        if (response.isSuccessful){
            val bytes = response.body()?.bytes() ?: return null //converts raw bytes to byte string
            return BitmapFactory.decodeByteArray(bytes, 0,bytes.size) //converts to bitmap
        }
        return null
    }
    suspend fun getAudio(): ByteArray?{
        val response = RetrofitInstance.api.getAudio()
        if (response.isSuccessful){
            val bytes = response.body()?.bytes() ?: return null
            return bytes
        }
        return null
    }
}
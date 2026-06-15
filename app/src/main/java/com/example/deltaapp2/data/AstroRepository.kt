package com.example.deltaapp2.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Canvas
import com.caverock.androidsvg.SVG
import com.example.deltaapp2.Models.AstroSkins
import com.example.deltaapp2.Models.AuthReq
import com.example.deltaapp2.Models.AuthResponse
import com.example.deltaapp2.Models.Leaderboard
import com.example.deltaapp2.Models.PowerUp
import com.example.deltaapp2.Models.PowerUpReq
import com.example.deltaapp2.Models.UIcolor
import com.example.deltaapp2.Models.messageLeader
import com.example.deltaapp2.Models.postLeaderboard
import okhttp3.ResponseBody
import retrofit2.Response

class AstroRepository {
    suspend fun getColor(): UIcolor {
        return RetrofitInstance.api.getColor()
    }
    suspend fun getSkins(): AstroSkins{
        return RetrofitInstance.api.getSkins()
    }
    suspend fun getPowerUps(req: PowerUpReq):List<PowerUp>{
        return RetrofitInstance.api.getPowerUps(req)
    }
    suspend fun getCustomSkin(path:String):Bitmap?{
        val response = RetrofitInstance.api.getCustomSkin(path)
        if (response.isSuccessful){
            val svgString = response.body()?.string() ?: return null
            val svg = SVG.getFromString(svgString)
            val width = svg.documentWidth.takeIf { it > 0 }?.toInt() ?: 200
            val height = svg.documentHeight.takeIf { it > 0 }?.toInt() ?: 200
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            svg.renderToCanvas(canvas)
            return bitmap
        }
        return null
    }
    suspend fun getPositions():List<List<Float>>{
        val response = RetrofitInstance.api.getPositions()
       return response.layout
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
    suspend fun getLeaders():List<Leaderboard>{
        return RetrofitInstance.api.getLeaders()
    }
    suspend fun postLeader(req: postLeaderboard,token:String): messageLeader {
        return RetrofitInstance.api.postLeader(req,token)
    }
}
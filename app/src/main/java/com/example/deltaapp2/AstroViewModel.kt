package com.example.deltaapp2

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import retrofit2.HttpException
import com.example.deltaapp2.Models.Asteroid
import com.example.deltaapp2.Models.AstroSkins
import com.example.deltaapp2.Models.AuthReq
import com.example.deltaapp2.Models.Bullet
import com.example.deltaapp2.Models.ErrorResponse
import com.example.deltaapp2.Models.Ship
import com.example.deltaapp2.Models.UIcolor
import com.example.deltaapp2.Models.User
import com.example.deltaapp2.data.AstroRepository
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class AstroViewModel: ViewModel() {
    private val repository = AstroRepository()
    var color by mutableStateOf<String>("")

    var gameOver by mutableStateOf(false)
    var skins by mutableStateOf<AstroSkins?>(null)
    var ship by mutableStateOf<Ship>(Ship(0f,0f,0f))
    var gameStartTime by mutableStateOf(System.currentTimeMillis())


    //to reset
    var score by mutableStateOf(0)
    var waveNumber by mutableStateOf(1)
    var currentTime by mutableStateOf(System.currentTimeMillis())
    var elapsedMillis = currentTime - gameStartTime
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var AuthError by mutableStateOf<String>("")
    var jwttoken = ""
    var user:User? = null
    var bullets by mutableStateOf<List<Bullet>>(listOf())

    var asteroids by mutableStateOf<List<Asteroid>>(listOf())

    var bulletSpeed by mutableStateOf(20f)
    var joystickOffset by mutableStateOf(Offset.Zero)

    var screenWidth by mutableStateOf(0f)
    var screenHeight by mutableStateOf(0f)
    //background, audio fetching
    var background by mutableStateOf<Bitmap?>(null)
    var audioBytes by mutableStateOf<ByteArray?>(null)
    var isLoadingTheme by mutableStateOf(false)
        private set
    var mediaPlayer: MediaPlayer? = null

    init{
        loadColor()
        loadSkins()
    }

    fun addAsteroids(){
        asteroids = asteroids + Asteroid(0.12f,0.85f,0.002f,-0.0015f,true)
    }

    fun asteroidShipCollision(){
        for (asteroid in asteroids) {
            val asteroidX = asteroid.x * screenWidth
            val asteroidY = asteroid.y * screenHeight
            val radius =
                if (asteroid.isLarge) 25f
                else 12f
            val dx = asteroidX - ship.x
            val dy = asteroidY - ship.y
            val distance = kotlin.math.sqrt((dx*dx)+(dy*dy))
            if (distance<radius) {
                gameOver=true
            }

        }
    }
    fun asteroidBulletCollision(){
        val asteroidsRemove = mutableListOf<Asteroid>()
        val bulletsRemove = mutableListOf<Bullet>()
        val asteroidsAdd = mutableListOf<Asteroid>()
        for (asteroid in asteroids){
            val asteroidX = asteroid.x * screenWidth
            val asteroidY = asteroid.y * screenHeight
            val radius =
                if (asteroid.isLarge) 25f
                else 12f
            for (bullet in bullets){
                val dx= bullet.x - asteroidX
                val dy = bullet.y - asteroidY
                val distance = kotlin.math.sqrt((dx*dx)+(dy*dy))
                if (distance<radius){
                    asteroidsRemove.add(asteroid)
                    bulletsRemove.add(bullet)
                    if (asteroid.isLarge){
                        asteroidsAdd.add(
                            Asteroid(
                                asteroid.x,
                                asteroid.y,
                                asteroid.vx,
                                -asteroid.vy,
                                false))
                        asteroidsAdd.add(Asteroid(
                            asteroid.x,
                            asteroid.y,
                            -asteroid.vx,
                            asteroid.vy,
                            false))
                    }
                    break
                }
            }
        }
        asteroids = asteroids - asteroidsRemove
        bullets = bullets - bulletsRemove
        asteroids = asteroids + asteroidsAdd
    }
    fun updateAsteroidPosition() {
        asteroids = asteroids.map {
            it.copy(x = it.x + it.vx, y = it.y + it.vy) }
            .filter { it.x in -0.2f..1.2f && it.y in -0.2f..1.2f }
    }
    fun updateBulletPosition() {
        bullets = bullets.map {
            it.copy(
                x = it.x + it.vx,
                y = it.y + it.vy
            )
        }
    }

    fun pointShipTowards(offset: Offset) {

        val angle = //use normal x-y coordinates logic for this
            Math.toDegrees(
                atan2( //atan works only for 1st quadrant, atan2 for all 4 quadrants, i.e, 360 degree tan(y/x)
                    offset.y.toDouble(),
                    offset.x.toDouble()
                )
            ).toFloat() //converting to degrees and finally to float

        ship = ship.copy(
            angle = angle + 90f //since triangle is pointing upward, default is not 0 degree but 90
        )
    }

    fun fireBullet(){
        val angleRad = Math.toRadians(ship.angle.toDouble())
        val tipX = ship.x + 50f * sin(angleRad).toFloat()
        val tipY = ship.y - 50f * cos(angleRad).toFloat()
        bullets=bullets+ Bullet(tipX,tipY,   //compose rotate uses degrees
            bulletSpeed * sin(angleRad).toFloat(),
            -bulletSpeed * cos(angleRad).toFloat())
    }
    fun createShip(x:Float,y:Float){
        if(ship.x == 0f && ship.y == 0f) {
            ship = ship.copy(
                x = x,
                y = y
            )
        }
    }

     fun register(){
        viewModelScope.launch{
            try{
                val req = AuthReq(username,password)
                val response = repository.doRegister(req)
                jwttoken = response.token
                User(username,jwttoken)
            }
            catch(e: HttpException){
                AuthError = try{
                    val error = e.response()?.errorBody()?.string()
                    Gson().fromJson(error, ErrorResponse::class.java).message
                }
                catch(e: Exception){
                    "Registration Error"
                }
            }
            catch(e: Exception){
                AuthError = "Some network Error"
            }
        }
    }
    fun login(){
        viewModelScope.launch{
            try{
                val req = AuthReq(username,password)
                val response = repository.doLogin(req)
                jwttoken = response.token
                user = User(username,jwttoken)
            }
            catch(e: HttpException){
                AuthError = try{
                    val error = e.response()?.errorBody()?.string()
                    Gson().fromJson(error, ErrorResponse::class.java).message
                }
                catch(e: Exception){
                    "Login Failed"
                }
            }
            catch(e:Exception){
                AuthError = "Some Network Error"
            }
        }
    }
    fun getThemePack(){
        viewModelScope.launch{
            isLoadingTheme = true
            val bgDeffered = async{
                repository.getBg()
            }
            val audioDeffered = async {
                repository.getAudio()
            }
            background = bgDeffered.await()
            audioBytes  = audioDeffered.await()
            isLoadingTheme = false
        }
    }
    private fun loadColor(){
        viewModelScope.launch {
            try {
                color = (repository.getColor()).color
            }
            catch(e: Exception){
                Log.e("SKINS", "Failed", e)
            }
        }
    }
    private fun loadSkins(){
        viewModelScope.launch {
            try {
                skins = repository.getSkins()
            }
            catch(e: Exception){
                Log.e("SKINS", "Failed", e)
            }
        }
    }
}
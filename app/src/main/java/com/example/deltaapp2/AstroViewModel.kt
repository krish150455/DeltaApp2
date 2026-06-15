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
import androidx.lifecycle.viewmodel.compose.viewModel
import retrofit2.HttpException
import com.example.deltaapp2.Models.Asteroid
import com.example.deltaapp2.Models.AstroSkins
import com.example.deltaapp2.Models.AuthReq
import com.example.deltaapp2.Models.Bullet
import com.example.deltaapp2.Models.ErrorResponse
import com.example.deltaapp2.Models.Leaderboard
import com.example.deltaapp2.Models.PowerUp
import com.example.deltaapp2.Models.PowerUpReq
import com.example.deltaapp2.Models.Ship
import com.example.deltaapp2.Models.UIcolor
import com.example.deltaapp2.Models.User
import com.example.deltaapp2.Models.postLeaderboard
import com.example.deltaapp2.data.AstroRepository
import com.example.deltaapp2.data.RetrofitInstance
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

class AstroViewModel: ViewModel() {
    private val repository = AstroRepository()
    var color by mutableStateOf<String>("")

    //GYROSCOPE CHECK:
    var isGyro by mutableStateOf(false)

    //SKINS:
    var skins by mutableStateOf<AstroSkins?>(null)
    var spaceshipskin by mutableStateOf("")
    var asteroidskin by mutableStateOf("")
    var ufoskin by mutableStateOf("")
    var shipBitmap by mutableStateOf<Bitmap?>(null)
    var asteroidBitmap by mutableStateOf<Bitmap?>(null)
    var ufoBitmap by mutableStateOf<Bitmap?>(null)


    var ship by mutableStateOf<Ship>(Ship(0f,0f,0f))

    //GAME TIME and WAVES CONTROL
    var elapsedMillis by mutableStateOf(0L)
    var gameStarted by mutableStateOf(false)
    var gameOver by mutableStateOf(false)
    var waveNumber by mutableStateOf(1)
    var wavePause by mutableStateOf(false)
    var savedTime by mutableStateOf(0L)
    var showDialog by mutableStateOf(false)
    var selectedPowerUp by mutableStateOf<PowerUp?>(null)
    var powerUpList by mutableStateOf<List<PowerUp>>(listOf())
    private var timerJob: Job? = null
    fun timeElapsed(){
        if (timerJob?.isActive == true) return
        timerJob=viewModelScope.launch {
            var gameStartTime = System.currentTimeMillis()-savedTime
            while (gameStarted) {
                if (!wavePause){
                    elapsedMillis = System.currentTimeMillis()-gameStartTime}

                delay(1000)
                }
        }
    }
    fun afterPowerUp(){
        viewModelScope.launch{
            applyPowerUp()
            showDialog = false
            wavePause = true
            addAsteroids()
            waveActive = true
            wavePause = false
        }
    }
    var hasShield by mutableStateOf(false)

    var scatterShot by mutableStateOf(false)

    var chronoMatrix by mutableStateOf(false)

    var empShockwave by mutableStateOf(false)
    fun applyPowerUp() {
        val powerUp = selectedPowerUp ?: return
        when (powerUp.name) {

            "Deflector Shield" -> {
                hasShield = true

            }

            "Scatter Shot" -> {
                viewModelScope.launch {
                    scatterShot = true
                    delay(powerUp.duration.milliseconds)
                    scatterShot = false
                }
            }

            "Chrono-Matrix" -> {
                viewModelScope.launch {
                    chronoMatrix = true
                    delay(powerUp.duration.milliseconds)
                    chronoMatrix = false
                }
            }

            "EMP Shockwave" -> {
                viewModelScope.launch {
                    empShockwave = true
                    delay(powerUp.duration.milliseconds)
                    empShockwave = false
                }
            }
        }
    }


    //to reset
    var score by mutableStateOf(0)
    var asteroid_score by mutableStateOf(0)


    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var AuthError by mutableStateOf<String>("")
    var jwttoken = ""
    var user by mutableStateOf<User?>(null)
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


    //CORE GAME LOGIC
    suspend fun addAsteroids(){

            var asteroidPositions = repository.getPositions()
            asteroidPositions = asteroidPositions.slice(0..(2*waveNumber))
            for (item in asteroidPositions) { //keep control on asteroids emitted with wavenumber
                asteroids = asteroids + Asteroid(item[0], item[1], 0.002f, -0.0015f, true)
            }

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
            if (distance < radius) {
                if (hasShield) {
                    hasShield = false
                    continue
                }
                gameOver = true
            }

        }
    }

    var waveActive by mutableStateOf(false)
    fun asteroidBulletCollision(){
        val asteroidsRemove = mutableListOf<Asteroid>()
        val bulletsRemove = mutableListOf<Bullet>()
        val asteroidsAdd = mutableListOf<Asteroid>()
        for (asteroid in asteroids){
            val asteroidX = asteroid.x * screenWidth
            val asteroidY = asteroid.y * screenHeight
            val radius =
                if (asteroid.isLarge) 25f
                else 12f       //even after moving to 90px and 45px asteroid bitmaps,
            // even tho collision circles are about half the size they should be, but the game still "works" because bullets often pass through the center area.
            for (bullet in bullets){
                val dx= bullet.x - asteroidX
                val dy = bullet.y - asteroidY
                val distance = kotlin.math.sqrt((dx*dx)+(dy*dy))
                if (distance<radius){
                    asteroid_score++
                    asteroidsRemove.add(asteroid)
                    bulletsRemove.add(bullet)
                    if (asteroid.isLarge){
                        score+=100
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
                    else if(!asteroid.isLarge){
                        score+=50
                    }
                    break
                }
            }
        }
        asteroids = asteroids - asteroidsRemove
        bullets = bullets - bulletsRemove
        asteroids = asteroids + asteroidsAdd
        if (asteroids.isEmpty() &&
            waveActive &&
            !showDialog &&
            !wavePause) {
            waveActive = false
            waveNumber++
            savedTime = elapsedMillis
            wavePause = true
            powerUpList = emptyList()
            getPowerUps(waveNumber)
            showDialog = true
        }
    }
    fun updateAsteroidPosition() {
        asteroids = asteroids.map { asteroid ->
            val speedMultiplier =
                if (chronoMatrix) 0.5f
                else 1f
            var newX = asteroid.x + asteroid.vx*speedMultiplier
            var newY = asteroid.y + asteroid.vy*speedMultiplier
            if (newX > 1f) newX = 0f
            if (newX < 0f) newX = 1f
            if (newY > 1f) newY = 0f
            if (newY < 0f) newY = 1f
            asteroid.copy(x = newX, y = newY)
        }
        if (empShockwave) {
            asteroids = asteroids.filter {
                it.isLarge
            }
        }
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
        val angleRad = Math.toRadians(ship.angle.toDouble()+90)
        val tipX =
            ship.x + 50f * sin(angleRad).toFloat()

        val tipY =
            ship.y - 50f * cos(angleRad).toFloat()
        if (!scatterShot){
            bullets = bullets + Bullet(
                tipX,
                tipY,
                bulletSpeed * sin(angleRad).toFloat(),
                -bulletSpeed * cos(angleRad).toFloat())
        }
        else {
            bullets = bullets + Bullet(
                tipX, tipY,   //compose rotate uses degrees
                bulletSpeed * sin(angleRad).toFloat(),
                -bulletSpeed * cos(angleRad).toFloat()
            )
            bullets = bullets + Bullet(
                tipX, tipY,   //compose rotate uses degrees
                bulletSpeed * sin(angleRad + Math.toRadians(15.0)).toFloat(),
                -bulletSpeed * cos(angleRad + Math.toRadians(15.0)).toFloat()
            )
            bullets = bullets + Bullet(
                tipX, tipY,   //compose rotate uses degrees
                bulletSpeed * sin(angleRad - Math.toRadians(15.0)).toFloat(),
                -bulletSpeed * cos(angleRad - Math.toRadians(15.0)).toFloat()
            )
        }
    }
    fun createShip(x:Float,y:Float){
        if(ship.x == 0f && ship.y == 0f) {
            ship = ship.copy(
                x = x,
                y = y
            )
        }
    }


    //LOGIN REGISTER
     fun register(){
        viewModelScope.launch{
            try{
                val req = AuthReq(username,password)
                val response = repository.doRegister(req)
                jwttoken = response.token
                user = User(username,jwttoken)
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
                Log.d("AUTH", "Before assignment: $user")
                user = User(username,jwttoken)
                Log.d("AUTH", "User assigned = $user")
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
    fun startNewGame() {
        viewModelScope.launch {
            timerJob?.cancel()
            timerJob = null
            elapsedMillis = 0
            savedTime = 0
            waveNumber = 1
            score = 0
            hasShield = false
            scatterShot = false
            chronoMatrix = false
            empShockwave = false
            gameOver = false
            bullets = emptyList()
            asteroids = emptyList()
            showDialog = false
            wavePause = true
            waveActive = false
            selectedPowerUp = null
            powerUpList = emptyList()
            ship = Ship(0f, 0f, 0f)
            addAsteroids()
            waveActive = true
            wavePause = false
            gameStarted = true
            asteroid_score=0
            timeElapsed()
        }
    }
    fun loadColor(){
        viewModelScope.launch {
            val colorDeffered=
            async {
                 repository.getColor()
            }
            try{
                color = colorDeffered.await().color
                }
            catch(e: Exception){
                Log.e("COLOR", "Failed", e)
            }
        }
    }
    fun loadSkins(){
        if (
            shipBitmap != null &&
            asteroidBitmap != null &&
            ufoBitmap != null
        ) return
        viewModelScope.launch {
                skins = repository.getSkins()
            val shipName =
                skins!!.spaceship.substringAfterLast("/")

            val asteroidName =
                skins!!.asteroid.substringAfterLast("/")

            val ufoName =
                skins!!.ufo.substringAfterLast("/")
            shipBitmap =
                repository.getCustomSkin(shipName)

            asteroidBitmap =
                repository.getCustomSkin(asteroidName)

            ufoBitmap =
                repository.getCustomSkin(ufoName)
        }
    }

    private var loadingPowerUps = false
    fun getPowerUps(wavenum:Int){
        if (loadingPowerUps) return
        loadingPowerUps = true
        viewModelScope.launch {
            try {
                powerUpList = repository.getPowerUps(PowerUpReq(true, wavenum))
            }
            catch(e:Exception){
                Log.e("API", "Failed", e)
            }
            finally {
                loadingPowerUps = false
            }
        }
    }
    //LEADERBOARD THINGS
    var leaderBoard by mutableStateOf<List<Leaderboard>>(listOf())
    fun getLeaderBoard(){
        viewModelScope.launch{
            leaderBoard = repository.getLeaders()
        }
    }
    suspend fun postLeader(req: postLeaderboard,token:String){
        try {
            repository.postLeader(req, "$token")
        }
        catch (e: HttpException) {
            Log.e("POST", "Code = ${e.code()}")
            Log.e(
                "POST",
                "ErrorBody = ${e.response()?.errorBody()?.string()}"
            )
        }
    }
}
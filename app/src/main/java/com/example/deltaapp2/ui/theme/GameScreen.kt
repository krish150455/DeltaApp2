package com.example.deltaapp2.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deltaapp2.AstroViewModel
import com.example.deltaapp2.Models.Bullet
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deltaapp2.Models.PowerUp
import com.example.deltaapp2.Models.postLeaderboard
import java.io.File
@Composable
fun GameScreen(viewModel: AstroViewModel,navController: NavController) {
    val context = LocalContext.current
    val sensorManager =
        remember {
            context.getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager
        }

    val gyroSensor =
        remember {
            sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE
            )
        }
    val gyroListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(
                event: SensorEvent
            ) {
                if (!viewModel.isGyro) return
                val x = event.values[0]
                val y = event.values[1]
                viewModel.ship =
                    viewModel.ship.copy(
                        angle = viewModel.ship.angle +
                                event.values[1] * 5f
                    )
            }
            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {
            }
        }
    }

    DisposableEffect(viewModel.isGyro) {

        if (viewModel.isGyro) {

            sensorManager.registerListener(
                gyroListener,
                gyroSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(
                gyroListener
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadColor()
        viewModel.getThemePack()
        viewModel.asteroids = emptyList()
    }
    LaunchedEffect(viewModel.audioBytes) {
        val bytes = viewModel.audioBytes ?: return@LaunchedEffect
        val file = File(context.cacheDir, "theme_music.mp3")
        file.writeBytes(bytes)
        if (viewModel.mediaPlayer==null) {
            viewModel.mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                isLooping = true
                prepare()
                start()
            }
        }
    }
    LaunchedEffect(viewModel.gameOver, viewModel.elapsedMillis) {
        if (viewModel.gameOver || viewModel.elapsedMillis >= 30_000) {
            viewModel.gameStarted = false

            viewModel.postLeader(postLeaderboard(viewModel.username, viewModel.score),viewModel.jwttoken)
            navController.navigate("home")

        }
    }
    DisposableEffect(Unit) { //to stop music on exiting the screen
        onDispose {
            viewModel.mediaPlayer?.stop()
            viewModel.mediaPlayer?.release()
            viewModel.mediaPlayer = null
        }
    }
    if (!viewModel.isLoadingTheme) {
        LaunchedEffect(Unit) {
            viewModel.startNewGame()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black).padding(20.dp)
        ) {
            LaunchedEffect(Unit) {
                while (true) {
                    if (!viewModel.wavePause) {
                        viewModel.updateBulletPosition()
                        viewModel.updateAsteroidPosition()
                        viewModel.asteroidBulletCollision()
                    }
                    delay(16)
                }
            }

            Canvas( //note center, size.width, size.height
                modifier = Modifier.fillMaxSize() //entire screen for drawing
            ) {
                viewModel.screenHeight = size.height
                viewModel.screenWidth = size.width
                val centerX = size.width / 2
                val centerY = size.height / 2
                viewModel.createShip(centerX, centerY)
                val ship = viewModel.ship
                viewModel.background?.let { bitmap ->
                    drawImage(
                        image = bitmap.asImageBitmap(),
                        dstSize = IntSize(
                            size.width.toInt(),
                            size.height.toInt()
                        )
                    )
                }
                rotate(
                    degrees = ship.angle, //pivot is centre of triangle, rotating about it
                    pivot = Offset(
                        ship.x,
                        ship.y
                    ) //default is origin Offset is like a class holding X,Y coordinates
                ) {
                    viewModel.shipBitmap?.let { bitmap ->
                        val shipSize = 170
                        drawImage(
                            image = bitmap.asImageBitmap(),
                            dstOffset = IntOffset(
                                (ship.x - shipSize / 2).toInt(),
                                (ship.y - shipSize / 2).toInt()
                            ), dstSize = IntSize(
                                shipSize,
                                shipSize
                            )
                        )
                    }
                }
                viewModel.bullets.forEach { item ->
                    drawCircle(
                        color = if (viewModel.color.isNotEmpty())
                            Color(android.graphics.Color.parseColor(viewModel.color))
                        else
                            Color.White,
                        radius = 8f,
                        center = Offset(
                            item.x,
                            item.y
                        )
                    )
                }
                viewModel.asteroids.forEach { item ->
                    val screenX = item.x * size.width
                    val screenY = item.y * size.height
                    if (item.isLarge) {
                        val size = 90f
                        viewModel.asteroidBitmap?.let { bitmap ->
                            drawImage(
                                image = bitmap.asImageBitmap(),
                                dstOffset = IntOffset(
                                    (screenX - size / 2).toInt(),
                                    (screenY - size / 2).toInt()
                                ), dstSize = IntSize(
                                    size.toInt(),
                                    size.toInt()
                                )
                            )
                        }
                    } else {
                        val size = 45f
                        viewModel.asteroidBitmap?.let { bitmap ->
                            drawImage(
                                image = bitmap.asImageBitmap(),
                                dstOffset = IntOffset(
                                    (screenX - size / 2).toInt(),
                                    (screenY - size / 2).toInt()
                                ), dstSize = IntSize(
                                    size.toInt(),
                                    size.toInt()
                                )
                            )
                        }
                    }
                }
            }
            if (!viewModel.isGyro){
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(end = 24.dp, bottom = 40.dp)
                    .size(120.dp) //SIZE PROB
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                viewModel.joystickOffset = Offset.Zero //When finger lifted
                            } //joystick nob returns back to center
                        ) { _, dragAmount ->
                            val newOffset =
                                viewModel.joystickOffset + dragAmount //updates with each lil drag
                            val maxRadius = 70f
                            if (newOffset.getDistance() <= maxRadius) { //to make check it doesn't go out circle
                                viewModel.joystickOffset =
                                    newOffset //if yes, makes that the new position of joystick knob
                                viewModel.pointShipTowards(
                                    newOffset
                                )
                            }
                        }
                    }
            ) {

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(
                        color = Color.Gray,
                        radius = 70f,
                        center = center, //center of given size150.dp
                        style = Stroke(width = 4f)
                    )

                    drawCircle(
                        color = Color.Gray,
                        radius = 40f,
                        center = center,
                        style = Stroke(width = 3f)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 18f,
                        center = center + viewModel.joystickOffset
                    )
                }

            }
        }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 24.dp, bottom = 40.dp)
                    .size(120.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            viewModel.fireBullet()
                        }
                    }
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(
                        color = Color.Gray,
                        radius = size.width / 4,
                        style = Stroke(width = 4f)
                    )
                }
                Text(
                    text = "FIRE",
                    color = Color.White,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalSeconds = viewModel.elapsedMillis / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                Column() {
                    Text(
                        text = "SCORE",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format("%06d", viewModel.score),
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format(
                            "%02d:%02d:%02d",
                            hours,
                            minutes,
                            seconds
                        ),
                        fontSize = 30.sp,
                        color = Color.White
                    )
                    Text(
                        text = "WAVE ${viewModel.waveNumber}",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ASTEROIDS",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${viewModel.asteroid_score}",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
            if (viewModel.showDialog && viewModel.powerUpList.isNotEmpty()){
                val Options = viewModel.powerUpList
                SingleSelectDialog(title="SELECT POWERUP",options=Options,
                    initialSelectedOption = Options.first().name ,
                    onDismissRequest = {  },
                    onConfirm = { selected ->
                        viewModel.selectedPowerUp = selected
                        viewModel.showDialog = false
                        viewModel.afterPowerUp()
                    })
            }

        }
    }
    else if(viewModel.isLoadingTheme) {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
                CircularProgressIndicator(modifier = Modifier.size(120.dp).align(Alignment.Center),
                    strokeWidth = 10.dp)
        }
    }

    }

@Composable
fun SingleSelectDialog(
    title: String,
    options: List<PowerUp>,
    initialSelectedOption: String,
    onDismissRequest: () -> Unit,
    onConfirm: (PowerUp) -> Unit
) {

    var selectedOption by remember {
        mutableStateOf(options.first())
    }

    AlertDialog(
        onDismissRequest = onDismissRequest, title = {
            Text(text = title) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row() {
                                RadioButton(
                                    selected = option == selectedOption,
                                    onClick = {
                                        selectedOption = option
                                    }
                                )
                                Text(
                                    text = option.name,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            Text(
                                text=option.description
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedOption)
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeltaApp2Theme {
        val navController = rememberNavController()
        var viewModel: AstroViewModel = viewModel()
        GameScreen(viewModel=viewModel,navController=navController)
    }
}
package com.example.deltaapp2.ui.theme

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
import androidx.compose.ui.Alignment
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import java.io.File
@Composable
fun GameScreen(viewModel: AstroViewModel) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getThemePack()
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
    DisposableEffect(Unit) { //to stop music on exiting the screen
        onDispose {
            viewModel.mediaPlayer?.stop()
            viewModel.mediaPlayer?.release()
            viewModel.mediaPlayer = null
        }
    }
    if (!viewModel.isLoadingTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black).padding(20.dp)
        ) {
            LaunchedEffect(Unit) {
                while (true) {
                    viewModel.updateBulletPosition()
                    viewModel.updateAsteroidPosition()
                    viewModel.asteroidBulletCollision()
                    viewModel.currentTime = System.currentTimeMillis()
                    delay(16)
                }
            }
            LaunchedEffect(Unit) {
                while (true) {
                    viewModel.addAsteroids()
                    delay(2000)
                }
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
                        text = "${viewModel.score}",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
                val path = Path().apply {
                    moveTo(ship.x, ship.y - 50)
                    lineTo(ship.x - 30, ship.y + 50)
                    lineTo(ship.x + 30, ship.y + 50)
                    close()
                }
                viewModel.background?.let { bitmap ->
                    drawImage(
                        image = bitmap.asImageBitmap(),
                        dstSize = IntSize(
                            size.width.toInt(),
                            size.height.toInt()
                        ),
                        modifier=Modifier.fillMaxSize()
                    )
                }
                rotate(
                    degrees = ship.angle, //pivot is centre of triangle, rotating about it
                    pivot = Offset(
                        ship.x,
                        ship.y
                    ) //default is origin Offset is like a class holding X,Y coordinates
                ) {
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 4f) //to have outline alone
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(
                            ship.x - 28,
                            ship.y + 32
                        ),
                        end = Offset(
                            ship.x + 28,
                            ship.y + 32
                        ),
                        strokeWidth = 4f
                    )
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

                    drawCircle(
                        color = Color.White,
                        radius = if (item.isLarge) 25f else 12f,
                        center = Offset(screenX, screenY)
                    )
                }
            }

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

        }
    }
    else if(viewModel.isLoadingTheme) {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
                CircularProgressIndicator(modifier = Modifier.size(120.dp).align(Alignment.Center),
                    strokeWidth = 10.dp)
        }
    }

    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeltaApp2Theme {
        var viewModel: AstroViewModel = viewModel()
        GameScreen(viewModel=viewModel)
    }
}
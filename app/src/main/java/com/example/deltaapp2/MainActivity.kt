package com.example.deltaapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.deltaapp2.ui.theme.DeltaApp2Theme
import kotlin.io.path.Path
import kotlin.io.path.moveTo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deltaapp2.Models.Ship
import com.example.deltaapp2.ui.theme.GameScreen
import com.example.deltaapp2.ui.theme.HomeScreen
import com.example.deltaapp2.ui.theme.LoginRegister
import com.example.deltaapp2.ui.theme.LoginScreen
import com.example.deltaapp2.ui.theme.RegisterScreen
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import java.io.File
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeltaApp2Theme {
                val navController = rememberNavController()
                val viewModel: AstroViewModel = viewModel()
                NavHost(navController=navController, startDestination = "Game Screen"){
                    composable("Login Register"){
                        LoginRegister(navController=navController)
                    }
                    composable("Login"){
                        LoginScreen(navController=navController,viewModel=viewModel)
                    }
                    composable("Register"){
                        RegisterScreen(navController=navController,viewModel=viewModel)
                    }
                    composable("Home"){
                        HomeScreen(navController=navController,viewModel=viewModel)
                    }
                    composable("Game Screen"){
                        GameScreen(viewModel=viewModel)
                    }

                }

            }
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
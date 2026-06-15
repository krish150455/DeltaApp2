package com.example.deltaapp2.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deltaapp2.AstroViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(navController: NavController,viewModel: AstroViewModel){
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val width = maxWidth
        val height = maxHeight
        LaunchedEffect(Unit) {
            viewModel.loadSkins()
        }
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.fillMaxWidth().align(Alignment.Center)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.9f).background(Color.White).padding(15.dp)
                    .clickable {
                    navController.navigate("Game Screen")
                }) {
                Text(
                    text = "PLAY >",
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color.Black
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().padding(15.dp)){
                Row(horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().weight(1f).background(Color.White).padding(15.dp)
                        .clickable{
                            navController.navigate("settings")
                        }){
                    Text(
                        text="SETTINGS",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize=20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(1f).weight(1f).background(Color.White).padding(15.dp).clickable{
                        viewModel.getLeaderBoard()
                        navController.navigate("Leader Board")
                    }){
                    Text(
                        text="LEADERBOARD",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize=20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RandPreview() {
    DeltaApp2Theme {
        var viewModel: AstroViewModel = viewModel()
        var navController= rememberNavController()
        HomeScreen(navController=navController,viewModel=viewModel)
    }
}
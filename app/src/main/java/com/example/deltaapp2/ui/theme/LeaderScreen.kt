package com.example.deltaapp2.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deltaapp2.AstroViewModel

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LeaderScreen(navController: NavController,viewModel: AstroViewModel){
    BoxWithConstraints(modifier=Modifier.fillMaxSize().background(Color.Black).padding(40.dp)) {
        Column(modifier=Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth().background(color=Color.White), horizontalArrangement = Arrangement.Center){
                Text(
                    text="LEADERBOARD",
                    fontWeight = FontWeight.Bold
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)){
                Row(modifier=Modifier.weight(1f).fillMaxWidth().weight(1f).background(Color.White),horizontalArrangement = Arrangement.Center){
                    Text(
                        text="USERNAME",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(modifier=Modifier.weight(1f).fillMaxWidth().weight(1f).background(Color.White),horizontalArrangement = Arrangement.Center){
                    Text(
                        text="SCORE",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(viewModel.leaderBoard) { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)){
                        Row(modifier=Modifier.weight(1f).fillMaxWidth().weight(1f).background(Color.White)){
                            Text(
                                text="${item.username}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(modifier=Modifier.weight(1f).fillMaxWidth().weight(1f).background(Color.White)){
                            Text(
                                text="${item.score}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeowPreview() {
    DeltaApp2Theme {
        var viewModel: AstroViewModel = viewModel()
        var navController= rememberNavController()
        LeaderScreen(navController=navController,viewModel=viewModel)
    }
}
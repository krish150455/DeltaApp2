package com.example.deltaapp2.ui.theme

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deltaapp2.AstroViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LoginRegister(navController: NavController) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(color = Color.Black)) {
        val width = maxWidth
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "ASTRO",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "DEFENDER",
                fontSize = 30. sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
            )
            Spacer(modifier = Modifier.height(80.dp))
            Button(
                onClick = { navController.navigate("Login") },
                modifier = Modifier.width(width / 2),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { navController.navigate("Register") },
                modifier = Modifier.width(width / 2),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "REGISTER",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LoginScreen(navController: NavController,viewModel: AstroViewModel){
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(color=Color.Black)) {
        val width = maxWidth
        LaunchedEffect(Unit) {
            viewModel.username = ""
            viewModel.password = ""
            viewModel.AuthError = ""
        }
        LaunchedEffect(viewModel.user) { //kinda use for navigating with state change
            if (viewModel.user != null) {
                navController.navigate("home")
            }
        }
        LaunchedEffect(viewModel.AuthError) {
            if (viewModel.AuthError.isNotEmpty()) {
                delay(5000)
                navController.navigate("Login Register")
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "USERNAME",
                fontSize = 15.sp,
                color = Color.Gray
            )
            TextField(
                value = viewModel.username,
                onValueChange = { text ->
                    viewModel.username = text
                },
                modifier = Modifier.width(width/2).height(56.dp),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                )
            )
            Text(
                text = "PASSWORD",
                fontSize = 15.sp,
                color = Color.Gray
            )
            TextField(
                value = viewModel.password,
                onValueChange = { text ->
                    viewModel.password = text
                },
                modifier = Modifier.width(width/2).height(56.dp),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            if (viewModel.AuthError.isNotEmpty()) {
                Text(
                    text = viewModel.AuthError,
                    color = Color.Red,
                    fontSize = 8.sp
                )
            }
            Spacer(modifier=Modifier.height(10.dp))
            Button(
                onClick = {
                    viewModel.login()
                }, modifier = Modifier.width(width/2),colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                enabled = if (viewModel.username.isNotBlank() && viewModel.password.isNotBlank()) true else false
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RegisterScreen(navController: NavController,viewModel: AstroViewModel){
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(color=Color.Black)) {
        val width = maxWidth
        LaunchedEffect(Unit) {
            viewModel.username = ""
            viewModel.password = ""
            viewModel.AuthError = ""
        }
        LaunchedEffect(viewModel.user) { //kinda use for navigating with state change and not with button
            Log.d("AUTH", "LaunchedEffect fired")
            Log.d("AUTH", "User = ${viewModel.user}")
            if (viewModel.user != null) {
                navController.navigate("Login")
            }
        }
        LaunchedEffect(viewModel.AuthError) {
            if (viewModel.AuthError.isNotEmpty()) {
                delay(5000)
                navController.navigate("Login Register")
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "USERNAME",
                fontSize = 15.sp,
                color = Color.Gray
            )
            TextField(   //BE CAREFUL WITH CONVERSIONS FOR TEXTFIELD
                value = viewModel.username,
                onValueChange = { text ->
                    viewModel.username = text
                },
                modifier = Modifier.width(width/2).height(56.dp),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                )
            )
            Text(
                text = "PASSWORD",
                fontSize = 15.sp,
                color = Color.Gray
            )
            TextField(   //BE CAREFUL WITH CONVERSIONS FOR TEXTFIELD
                value = viewModel.password,
                onValueChange = { text ->
                    viewModel.password = text
                },
                modifier = Modifier.width(width/2).height(56.dp),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
            if (viewModel.AuthError.isNotEmpty()) {
                Text(
                    text = viewModel.AuthError,
                    color = Color.Red,
                    fontSize = 8.sp
                )
            }
            Spacer(modifier=Modifier.height(10.dp))
            Button(
                onClick = {
                    viewModel.register()
                }, modifier = Modifier.width(width/2),colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                enabled = if (viewModel.username.isNotBlank() && viewModel.password.isNotBlank()) true else false
            ) {
                Text(
                    text = "REGISTER",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SomePreview() {
    DeltaApp2Theme {
        val navController = rememberNavController()
        val viewModel: AstroViewModel = viewModel()
        LoginScreen(navController,viewModel)
    }
}
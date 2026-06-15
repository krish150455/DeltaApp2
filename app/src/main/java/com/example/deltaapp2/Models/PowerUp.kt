package com.example.deltaapp2.Models

data class PowerUp(
    val id: Int,
    val name :String,
    val description:String,
    val duration: Int
)
data class PowerUpReq(
    val waveCleared:Boolean,
    val waveNumber: Int
)
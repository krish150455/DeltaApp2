package com.example.deltaapp2.Models

data class Bullet(
    var x:Float,
    var y:Float,
    var vx:Float, //vx = vcos0 vy=vsin0 where 0 is ship angle
    var vy:Float
)

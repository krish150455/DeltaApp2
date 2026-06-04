package com.example.deltaapp2.Models

data class AuthReq(
    val username:String,
    val password:String
)
data class User(
    val username: String,
    val token: String
)

data class AuthResponse(
    val token: String
)
data class ErrorResponse(
    val message:String
)
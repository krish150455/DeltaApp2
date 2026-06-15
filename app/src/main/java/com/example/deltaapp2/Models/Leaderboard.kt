package com.example.deltaapp2.Models

data class Leaderboard(
    val id: Int,
    val username: String,
    val score: Int,
    val CreatedAt: String
)
data class postLeaderboard(
    val name: String,
    val score: Int
)
data class messageLeader(
    val message: String
)
package com.lucasmbraz.topscorers.model

data class Player(
    val id: String,
    val name: String,
    val position: String,
    val team: String,
    val goals: Int,
    val picture: String
)
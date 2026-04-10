package com.domain.models

data class LetraSync(
    val id: Int = 0,
    val letraId: Int = 0,
    val timestamp: Int, // ms
    val texto: String
)

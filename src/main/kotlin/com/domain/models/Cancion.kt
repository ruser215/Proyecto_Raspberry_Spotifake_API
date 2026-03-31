package com.domain.models

data class Cancion(
    val id: Int,
    val nombre: String,
    val artista: Int?,
    val album: Int?,
    val genero: Int,
    val urlAudio: String,
    val likes: Int = 0,
    val reproducciones: Int = 0
)

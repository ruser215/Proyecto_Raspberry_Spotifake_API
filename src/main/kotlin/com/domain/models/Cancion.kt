package com.domain.models

data class Cancion(
    val id: Int,
    val nombre: String,
    val artistaId: Int? = null,
    val artistaIds: List<Int>? = null,
    val artista: String? = null,
    val albumId: Int? = null,
    val albumIds: List<Int>? = null,
    val album: String? = null,
    val genero: Int,
    val urlAudio: String,
    val urlPortada: String? = null,
    val likes: Int = 0,
    val reproducciones: Int = 0
)

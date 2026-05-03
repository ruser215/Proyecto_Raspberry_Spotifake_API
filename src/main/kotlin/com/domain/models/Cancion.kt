package com.domain.models

data class Cancion(
    val id: Int = 0,
    val nombre: String,
    val artistaIds: List<Int> = emptyList(), // Lista de artistas (N:M)
    val albumId: Int? = null,                // Un solo Álbum (1:N) para la portada
    val generosIds: List<Int> = emptyList(), // Lista de géneros (N:M)
    val urlAudio: String,
    val urlPortada: String? = null,
    val likes: Int = 0,
    val reproducciones: Int = 0,
    // Campos informativos para el cliente
    val artista: String? = null,
    val album: String? = null,
    val genero: Int? = null
)

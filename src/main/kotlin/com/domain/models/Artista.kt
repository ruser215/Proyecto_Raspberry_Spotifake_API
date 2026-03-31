package com.domain.models

data class Artista(
    val id: Int,
    val nombre: String,
    val fotoUrl: String?,
    val seguidores: Int = 0,
    val likesTotales: Int = 0
)

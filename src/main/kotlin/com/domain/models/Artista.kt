package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Artista(
    val id: Int = 0,
    val nombre: String,
    val fotoUrl: String?,
    val seguidores: Int = 0,
    val likesTotales: Int = 0,
    val siguiendo: Boolean = false,
    val albumIds: List<Int>? = null
)

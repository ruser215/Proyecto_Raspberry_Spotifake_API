package com.domain.models

data class Album(
    val id: Int,
    val nombre: String,
    val artistaId: Int? = null,
    val artistaIds: List<Int>? = null,
    val artista: String? = null,
    val portadaUrl: String? = null,
    val fechaLanzamiento: String? = null
)

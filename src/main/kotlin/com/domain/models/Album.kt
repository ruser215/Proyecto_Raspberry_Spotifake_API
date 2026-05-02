package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Int = 0,
    val nombre: String,
    val artistaId: Int? = null,
    val artistaIds: List<Int>? = null,
    val artista: String? = null,
    val portadaUrl: String? = null,
    val fechaLanzamiento: String? = null
)

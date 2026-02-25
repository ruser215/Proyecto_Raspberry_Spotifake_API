package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Artista(
    val id: Int? = null,
    val nombre: String,
    val fotoUrl: String? = null
)

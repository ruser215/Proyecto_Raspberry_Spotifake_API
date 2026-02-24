package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Artista(
    val nombre: String,
    val fotoUrl: String? = null
)

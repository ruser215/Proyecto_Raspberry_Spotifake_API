package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val nombre: String,
    val artista: String,
    val portadaUrl: String? = null
)

package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Cancion(
    val id: Int? = null,
    var nombre: String,
    var artista: String,
    var album: String,
    var audioUrl: String,
    var portadaUrl: String
)

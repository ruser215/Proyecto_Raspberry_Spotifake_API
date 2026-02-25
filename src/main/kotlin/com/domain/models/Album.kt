package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Int? = null,
    val nombre: String,
    /** referencia al identificador del artista dueño del álbum */
    val artistaId: Int? = null,
    val portadaUrl: String? = null
)

package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Int? = null,
    val nombre: String,
    val artistaId: Int? = null,
    val artistaIds: List<Int>? = emptyList(),
    val artistasNombre: List<String>? = emptyList(),
    val portadaUrl: String? = null
)

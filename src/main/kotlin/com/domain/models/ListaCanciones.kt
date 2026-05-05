package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ListaCanciones(
    val id: Long,
    val nombre: String,
    val idUsuario: Long
)

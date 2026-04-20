package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Genero(
    val id: Int = 0,
    val nombre: String
)

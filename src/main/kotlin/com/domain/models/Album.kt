package com.domain.models

data class Album(
    val id: Int,
    val nombre: String,
    val portadaUrl: String?,
    val fechaLanzamiento: String?
)

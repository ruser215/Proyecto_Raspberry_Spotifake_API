package com.domain.models

data class Letra(
    val id: Int,
    val cancionId: Int,
    val textoCompleto: String? = null,
    val lineas: List<LetraSync>? = null
)

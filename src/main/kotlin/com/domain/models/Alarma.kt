package com.domain.models

data class Alarma(
    val id: Int = 0,
    val userId: Long,
    val nombre: String,
    val hora: String, // "HH:mm"
    val cancionId: Int,
    val activo: Boolean = true
)

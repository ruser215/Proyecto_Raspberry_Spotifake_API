package com.domain.models

data class Mascota(
    val id: Int = 0,
    val nombre: String,
    val precio: Double,
    val urlSprite: String,
    val premiumDefault: Boolean,
    val tipoId: Int
)

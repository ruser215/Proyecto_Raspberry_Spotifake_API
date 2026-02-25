/**
 * Modelo de dominio de género musical.
 * Permite clasificar canciones por categoría.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Genero(
    val id: Int? = null,
    var nombre: String
)

/**
 * Modelo de dominio de lista de canciones.
 * Representa una colección de canciones creada por un usuario.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ListaCanciones(
    val id: Long? = null,
    var nombre: String = "",
    var idUsuario: Long = 0L
)

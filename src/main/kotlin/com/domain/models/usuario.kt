/**
 * Modelo de dominio de usuario.
 * Representa el estado principal de una cuenta dentro de la aplicación.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Long? = null,
    var username: String,
    var correo: String,
    var admin: Int = 0,
    var premium: Int = 0,
    var pass: String,
    var token: String? = null,
    var urlImagen: String? = null
)
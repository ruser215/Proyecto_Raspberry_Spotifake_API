/**
 * Modelo de dominio de usuario.
 * Representa el estado principal de una cuenta dentro de la aplicación.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Long? = null,
    var nombre: String,
    var apellido1: String,
    var apellido2: String,
    var correo: String,
    var admin: Boolean = false,
    var premium: Boolean = false,
    var pass: String,
    var token: String? = null,
    var urlImagen: String? = null
)
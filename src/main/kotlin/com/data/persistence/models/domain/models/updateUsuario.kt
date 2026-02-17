/**
 * DTO de actualización parcial de usuario.
 * Se usa en peticiones donde no es obligatorio enviar todos los campos.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsuario(
    var nombre: String? = null,
    var apellido1: String? = null,
    var apellido2: String? = null,
    var correo: String? = null,
    var admin: Boolean? = null,
    var premium: Boolean? = null,
    var pass: String? = null,
    var token: String? = null,
    var urlImagen: String? = null
)
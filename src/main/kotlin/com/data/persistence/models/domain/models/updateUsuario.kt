package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsuario(
    var nombre: String? = null,
    var apellido1: String? = null,
    var apellido2: String? = null,
    var nickname: String? = null,
    var contrasena: String? = null,
    var msg: String? = null  // Para transportar tokens JWT o mensajes
)
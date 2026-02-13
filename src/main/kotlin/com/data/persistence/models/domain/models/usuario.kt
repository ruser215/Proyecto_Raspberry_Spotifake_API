package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Int? = null,           // Opcional, ya que es autoincremental en la DB
    var nombre: String,
    var apellido1: String,
    var apellido2: String,
    var nickname: String,
    var contrasena: String,        // Mantener como var si necesitas manipularla antes de guardar
    var msg: String? = null        // Para transportar tokens JWT o mensajes
)
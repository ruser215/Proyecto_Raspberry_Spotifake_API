package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUsuario(
    val username: String? = null,
    val correo: String? = null,
    val admin: Boolean? = null,
    val premium: Boolean? = null,
    val pass: String? = null,
    val token: String? = null,
    val urlImagen: String? = null
)

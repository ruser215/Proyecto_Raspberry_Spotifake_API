data class Usuario(
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Long,
    val username: String,
    val correo: String,
    val admin: Boolean = false,
    val premium: Boolean = false,
    val pass: String,
    val token: String? = null,
    val urlImagen: String? = null
)

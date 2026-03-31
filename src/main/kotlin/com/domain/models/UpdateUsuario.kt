package com.domain.models

data class UpdateUsuario(
    val username: String?,
    val correo: String?,
    val admin: Boolean?,
    val premium: Boolean?,
    val pass: String?,
    val token: String?,
    val urlImagen: String?
)

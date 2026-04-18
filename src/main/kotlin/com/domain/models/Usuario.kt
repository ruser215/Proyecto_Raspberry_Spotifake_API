package com.domain.models

data class Usuario(
    val id: Int,
    val username: String,
    val correo: String,
    val admin: Boolean = false,
    val premium: Boolean = false,
    val pass: String,
    val token: String? = null,
    val urlImagen: String? = null
)

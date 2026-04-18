package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUsuario(
    val username: String?,
    val correo: String?,
    val pass: String?
)
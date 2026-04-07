package com.domain.models

import java.time.LocalDate

data class Anuncio(
    val id: Int? = null,
    val titulo: String,
    val descripcion: String,
    val urlBanner: String,
    val urlPoster: String,
    val urlAudio: String,
    val fecha: LocalDate,
    val idEmpresa: Int,
    val idCancion: Int,
    val activo: Boolean = true
)

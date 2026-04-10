package com.domain.models

import java.time.LocalDate

data class Reproduccion(
    val id: Int = 0,
    val idUsuario: Long,
    val idCancion: Int,
    val fecha: LocalDate = LocalDate.now(),
    val segundosEscuchados: Int
)

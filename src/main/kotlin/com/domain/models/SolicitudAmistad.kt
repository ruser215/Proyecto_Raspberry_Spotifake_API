package com.domain.models

import java.time.LocalDate

data class SolicitudAmistad(
    val id: Int = 0,
    val remitenteId: Long,
    val destinatarioId: Long,
    val estado: String,
    val fecha: LocalDate = LocalDate.now()
)

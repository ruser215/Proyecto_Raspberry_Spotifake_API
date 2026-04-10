package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object LetraTable : IntIdTable("letra") {
    val cancion = reference("id_cancion", CancionTable)
    val textoCompleto = text("texto_completo").nullable()
}

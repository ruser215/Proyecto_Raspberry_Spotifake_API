package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object LetraSyncTable : IntIdTable("letrasync") {
    val letra = reference("id_letra", LetraTable)
    val timestamp = integer("timestamp_ms")
    val texto = varchar("texto_linea", 500)
}

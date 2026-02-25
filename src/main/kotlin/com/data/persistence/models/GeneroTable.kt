package com.data.persistence.models

/**
 * Tabla de géneros musicales.
 */
import org.jetbrains.exposed.dao.id.IntIdTable

object GeneroTable : IntIdTable("generos") {
    val nombre = varchar("nombre", 100)
}

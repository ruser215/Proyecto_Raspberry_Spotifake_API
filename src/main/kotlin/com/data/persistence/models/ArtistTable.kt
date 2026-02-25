package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Tabla de artistas normalizada.
 */
object ArtistTable : IntIdTable("artistas") {
    val nombre = varchar("nombre", 200).uniqueIndex()
    val fotoUrl = varchar("fotourl", 255).nullable()
}

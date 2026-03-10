package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Tabla de artistas normalizada.
 */
object ArtistTable : IntIdTable("artistas") {
    val nombre = varchar("nombre", 200).uniqueIndex()
    val fotoUrl = varchar("fotourl", 255).nullable()
    val seguidores = integer("seguidores").default(0)
    val likesTotales = integer("likes_totales").default(0)
}

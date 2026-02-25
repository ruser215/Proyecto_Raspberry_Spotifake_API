package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Álbumes de artistas.
 */
object AlbumTable : IntIdTable("albums") {
    val nombre = varchar("nombre", 200)
    val artista = reference("artista", ArtistTable, onDelete = ReferenceOption.CASCADE)
    val portadaUrl = varchar("portadaurl", 255).nullable()
    init {
        index(true, nombre, artista)
    }
}

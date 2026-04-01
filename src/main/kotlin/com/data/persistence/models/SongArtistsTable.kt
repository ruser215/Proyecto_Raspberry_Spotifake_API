package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object SongArtistsTable : Table("artista_cancion") {
    val songId = reference("id_cancion", CancionTable)
    val artistId = reference("id_artista", ArtistaTable)
    override val primaryKey = PrimaryKey(songId, artistId)
}

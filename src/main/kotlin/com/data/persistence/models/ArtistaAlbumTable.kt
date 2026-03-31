package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object ArtistaAlbumTable : Table("artista_album") {
    val idArtista = reference("id_artista", ArtistaTable)
    val idAlbum = reference("id_album", AlbumTable)
    override val primaryKey = PrimaryKey(idArtista, idAlbum)
}

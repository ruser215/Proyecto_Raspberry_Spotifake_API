package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object AlbumCancionTable : Table("album_cancion") {
    val idAlbum = reference("id_album", AlbumTable)
    val idCancion = reference("id_cancion", CancionTable)
    override val primaryKey = PrimaryKey(idAlbum, idCancion)
}

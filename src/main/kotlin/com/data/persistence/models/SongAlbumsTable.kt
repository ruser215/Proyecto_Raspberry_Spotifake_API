package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object SongAlbumsTable : Table("album_cancion") {
    val songId = reference("id_cancion", CancionTable)
    val albumId = reference("id_album", AlbumTable)
    override val primaryKey = PrimaryKey(songId, albumId)
}

package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object PlaylistCancionTable : Table("playlist_cancion") {
    val idPlaylist = reference("id_playlist", PlaylistTable)
    val idCancion = reference("id_cancion", CancionTable)
    override val primaryKey = PrimaryKey(idPlaylist, idCancion)
}

package com.data.persistence.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ReferenceOption

object SongArtistsTable : Table("song_artists") {
    val songId = reference("song_id", CancionTable, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artist_id", ArtistTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(songId, artistId)
}

object SongAlbumsTable : Table("song_albums") {
    val songId = reference("song_id", CancionTable, onDelete = ReferenceOption.CASCADE)
    val albumId = reference("album_id", AlbumTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(songId, albumId)
}

object AlbumArtistsTable : Table("album_artists") {
    val albumId = reference("album_id", AlbumTable, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artist_id", ArtistTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(albumId, artistId)
}

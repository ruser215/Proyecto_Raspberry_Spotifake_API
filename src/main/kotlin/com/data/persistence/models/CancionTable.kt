package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object CancionTable : IntIdTable("canciones") {
    val nombre = varchar("nombre", 200)
    // referencias normalizadas
    val artista = reference("artista", ArtistTable, onDelete = ReferenceOption.CASCADE).nullable()
    val album = reference("album", AlbumTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val genero = reference("genero", GeneroTable)
    val likes = integer("likes").default(0)
    val urlAudio = varchar("urlaudio", 255)
    val urlPortada = varchar("urlportada", 255)
}

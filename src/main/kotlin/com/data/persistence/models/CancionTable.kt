
package com.data.persistence.models
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object CancionTable : IntIdTable("cancion") {
    val nombre = varchar("nombre", 200)
    val artista = reference("artista", ArtistaTable, onDelete = ReferenceOption.CASCADE).nullable()
    val album = reference("album", AlbumTable, onDelete = ReferenceOption.CASCADE).nullable()
    val genero = reference("genero", GeneroTable, onDelete = ReferenceOption.CASCADE)
    val urlAudio = varchar("url_audio", 300)
    val urlPortada = varchar("url_portada", 300).nullable()
    val likes = integer("likes").default(0)
    val reproducciones = integer("reproducciones").default(0)
}

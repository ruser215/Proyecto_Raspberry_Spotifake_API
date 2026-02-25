package com.data.persistence.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Cancion

class CancionDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CancionDao>(CancionTable)

    var nombre by CancionTable.nombre
    var artista by ArtistDao optionalReferencedOn CancionTable.artista
    var album by AlbumDao optionalReferencedOn CancionTable.album
    var genero by CancionTable.genero
    var likes by CancionTable.likes
    var urlAudio by CancionTable.urlAudio
    var urlPortada by CancionTable.urlPortada

    fun toCancion(): Cancion {
        return Cancion(
            id = this.id.value,
            nombre = this.nombre,
            artistaId = artista?.id?.value,
            albumId = album?.id?.value,
            artista = artista?.nombre ?: album?.artista?.nombre ?: "",
            album = album?.nombre ?: "",
            genero = this.genero.value,
            likes = this.likes,
            urlAudio = this.urlAudio,
            urlPortada = this.urlPortada
        )
    }
}

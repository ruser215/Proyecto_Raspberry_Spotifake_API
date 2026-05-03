
package com.data.persistence.models

import com.domain.models.Cancion
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CancionDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CancionDao>(CancionTable)

    var nombre by CancionTable.nombre
    var artista by ArtistDao optionalReferencedOn CancionTable.artista
    var album by AlbumDao optionalReferencedOn CancionTable.album
    var genero by CancionTable.genero
    var urlAudio by CancionTable.urlAudio
    var urlPortada by CancionTable.urlPortada
    var likes by CancionTable.likes
    var reproducciones by CancionTable.reproducciones

    fun toCancion() = Cancion(
        id = id.value,
        nombre = nombre,
        artistaIds = listOfNotNull(artista?.id?.value),
        albumId = album?.id?.value,
        generosIds = listOfNotNull(genero.value),
        artista = artista?.nombre,
        album = album?.nombre,
        genero = genero.value,
        urlAudio = urlAudio,
        urlPortada = urlPortada,
        likes = likes,
        reproducciones = reproducciones
    )
}

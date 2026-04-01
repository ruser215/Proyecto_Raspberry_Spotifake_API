
package com.data.persistence.models

import com.domain.models.Album
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AlbumDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AlbumDao>(AlbumTable)

    var nombre by AlbumTable.nombre
    var portadaUrl by AlbumTable.portadaUrl
    var artista by ArtistDao optionalReferencedOn AlbumTable.artista

    fun toAlbum() = Album(
        id = id.value,
        nombre = nombre,
        artistaId = artista?.id?.value,
        artista = artista?.nombre,
        portadaUrl = portadaUrl
    )
}

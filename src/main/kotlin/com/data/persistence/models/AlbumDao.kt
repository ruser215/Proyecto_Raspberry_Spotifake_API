package com.data.persistence.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Album

class AlbumDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AlbumDao>(AlbumTable)

    var nombre by AlbumTable.nombre
    var artista by ArtistDao referencedOn AlbumTable.artista
    var portadaUrl by AlbumTable.portadaUrl

    fun toAlbum(): Album {
        return Album(
            id = this.id.value,
            nombre = this.nombre,
            artistaId = this.artista.id.value,
            portadaUrl = this.portadaUrl
        )
    }
}

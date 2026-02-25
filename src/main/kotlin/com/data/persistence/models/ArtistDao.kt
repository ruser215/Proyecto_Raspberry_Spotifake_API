package com.data.persistence.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Artista

class ArtistDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ArtistDao>(ArtistTable)

    var nombre by ArtistTable.nombre
    var fotoUrl by ArtistTable.fotoUrl

    fun toArtista(): Artista {
        return Artista(
            id = this.id.value,
            nombre = this.nombre,
            fotoUrl = this.fotoUrl
        )
    }
}

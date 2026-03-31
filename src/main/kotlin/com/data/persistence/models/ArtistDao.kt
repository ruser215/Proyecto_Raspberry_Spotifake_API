package com.data.persistence.models

import com.domain.models.Artista
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ArtistDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ArtistDao>(ArtistaTable)

    var nombre by ArtistaTable.nombre
    var fotoUrl by ArtistaTable.fotoUrl
    var seguidores by ArtistaTable.seguidores
    var likesTotales by ArtistaTable.likesTotales

    fun toArtista() = Artista(
        id = id.value,
        nombre = nombre,
        fotoUrl = fotoUrl,
        seguidores = seguidores,
        likesTotales = likesTotales
    )
}

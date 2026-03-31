package com.data.persistence.models

import com.domain.models.Album
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AlbumDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AlbumDao>(AlbumTable)

    var nombre by AlbumTable.nombre
    var portadaUrl by AlbumTable.portadaUrl
    var fechaLanzamiento by AlbumTable.fechaLanzamiento

    fun toAlbum() = Album(
        id = id.value,
        nombre = nombre,
        portadaUrl = portadaUrl,
        fechaLanzamiento = fechaLanzamiento?.toString()
    )
}

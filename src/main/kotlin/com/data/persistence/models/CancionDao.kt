
package com.data.persistence.models
import com.domain.models.Cancion
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CancionDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CancionDao>(CancionTable)

    var nombre by CancionTable.nombre
    var artista by CancionTable.artista
    var album by CancionTable.album
    var genero by CancionTable.genero
    var urlAudio by CancionTable.urlAudio
    var likes by CancionTable.likes
    var reproducciones by CancionTable.reproducciones

    fun toCancion() = Cancion(
        id = id.value,
        nombre = nombre,
        artista = artista?.value,
        album = album?.value,
        genero = genero.value,
        urlAudio = urlAudio,
        likes = likes,
        reproducciones = reproducciones
    )
}

package com.data.persistence.models

/**
 * DAO de lista de canciones.
 */
import com.domain.models.ListaCanciones
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ListaCancionesDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ListaCancionesDao>(ListaCancionesTable)

    var nombre by ListaCancionesTable.nombre
    var idUsuario by ListaCancionesTable.idUsuario

    fun toListaCanciones(): ListaCanciones {
        return ListaCanciones(
            id = this.id.value,
            nombre = this.nombre,
            idUsuario = this.idUsuario.value
        )
    }
}

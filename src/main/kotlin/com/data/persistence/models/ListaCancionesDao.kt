package com.data.persistence.models

import com.domain.models.ListaCanciones
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ListaCancionesDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ListaCancionesDao>(ListaCancionesTable)

    var nombre by ListaCancionesTable.nombre
    var idUsuario by ListaCancionesTable.idUsuario

    fun toListaCanciones() = ListaCanciones(
        id = id.value,
        nombre = nombre,
        idUsuario = idUsuario.value
    )
}

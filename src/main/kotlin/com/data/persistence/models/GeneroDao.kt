package com.data.persistence.models

import com.domain.models.Genero
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GeneroDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GeneroDao>(GeneroTable)

    var nombre by GeneroTable.nombre

    fun toGenero() = Genero(
        id = id.value,
        nombre = nombre
    )
}

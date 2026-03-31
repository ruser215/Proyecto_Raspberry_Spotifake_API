package com.data.persistence.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TemaDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TemaDao>(TemaTable)

    var colorPrimario by TemaTable.colorPrimario
    var colorSecundario by TemaTable.colorSecundario
}

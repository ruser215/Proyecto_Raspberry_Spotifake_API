package com.data.persistence.models

import com.domain.models.Reproduccion
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ReproduccionDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReproduccionDao>(ReproduccionTable)

    var usuario by UsuarioDao referencedOn ReproduccionTable.idUsuario
    var cancion by CancionDao referencedOn ReproduccionTable.idCancion
    var fecha by ReproduccionTable.fecha
    var segundosEscuchados by ReproduccionTable.segundosEscuchados

    fun toReproduccion() = Reproduccion(
        id = id.value,
        idUsuario = usuario.id.value,
        idCancion = cancion.id.value,
        fecha = fecha,
        segundosEscuchados = segundosEscuchados
    )
}

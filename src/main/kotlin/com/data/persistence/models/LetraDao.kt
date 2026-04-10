package com.data.persistence.models

import com.domain.models.Letra
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LetraDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LetraDao>(LetraTable)

    var cancion by CancionDao referencedOn LetraTable.cancion
    var textoCompleto by LetraTable.textoCompleto
    val lineas by LetraSyncDao referrersOn LetraSyncTable.letra

    fun toLetra() = Letra(
        id = id.value,
        cancionId = cancion.id.value,
        textoCompleto = textoCompleto,
        lineas = lineas.map { it.toLetraSync() }
    )
}

package com.data.persistence.models

import com.domain.models.Mascota
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MascotaDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MascotaDao>(MascotaTable)

    var nombre by MascotaTable.nombre
    var precio by MascotaTable.precio
    var urlSprite by MascotaTable.urlSprite
    var activo by MascotaTable.activo
    var premiumDefault by MascotaTable.premiumDefault
    var tipo by MascotaTable.tipo

    fun toMascota() = Mascota(
        id = id.value,
        nombre = nombre,
        precio = precio.toDouble(),
        urlSprite = urlSprite,
        premiumDefault = premiumDefault,
        tipoId = tipo.value
    )
}

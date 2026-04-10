package com.data.persistence.models

import com.domain.models.Alarma
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AlarmaDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AlarmaDao>(AlarmaTable)

    var usuario by UsuarioDao referencedOn AlarmaTable.idUsuario
    var nombre by AlarmaTable.nombreAlarma
    var hora by AlarmaTable.hora
    var cancion by CancionDao referencedOn AlarmaTable.idCancion
    var activo by AlarmaTable.activo

    fun toAlarma() = Alarma(
        id = id.value,
        userId = usuario.id.value,
        nombre = nombre,
        hora = hora,
        cancionId = cancion.id.value,
        activo = activo
    )
}

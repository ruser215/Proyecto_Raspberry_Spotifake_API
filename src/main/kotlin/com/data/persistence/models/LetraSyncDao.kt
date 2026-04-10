package com.data.persistence.models

import com.domain.models.LetraSync
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LetraSyncDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LetraSyncDao>(LetraSyncTable)

    var letra by LetraDao referencedOn LetraSyncTable.letra
    var timestamp by LetraSyncTable.timestamp
    var texto by LetraSyncTable.texto

    fun toLetraSync() = LetraSync(
        id = id.value,
        letraId = letra.id.value,
        timestamp = timestamp,
        texto = texto
    )
}

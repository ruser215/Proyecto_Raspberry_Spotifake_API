package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object TipoMascotaTable : IntIdTable("tipo_mascota") {
    val tipo = varchar("tipo", 200)
}

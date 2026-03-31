package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object EmpresaTable : IntIdTable("empresa") {
    val nombre = varchar("nombre", 100)
    val urllogo = varchar("urllogo", 300).nullable()
}

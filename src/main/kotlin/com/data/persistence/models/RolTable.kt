package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object RolTable : IntIdTable("rol") {
    val rol = varchar("rol", 150)
}

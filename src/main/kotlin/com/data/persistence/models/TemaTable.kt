package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object TemaTable : IntIdTable("tema") {
    val colorPrimario = varchar("color_primario", 7)
    val colorSecundario = varchar("color_secundario", 7)
}

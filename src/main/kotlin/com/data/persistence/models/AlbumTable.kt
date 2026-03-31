package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AlbumTable : IntIdTable("album") {
    val nombre = varchar("nombre", 250)
    val portadaUrl = varchar("portada_url", 300).nullable()
    val fechaLanzamiento = date("fecha_lanzamiento")
}

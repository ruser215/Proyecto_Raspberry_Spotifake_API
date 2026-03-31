package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object ArtistaTable : IntIdTable("artista") {
    val nombre = varchar("nombre", 200).uniqueIndex()
    val fotoUrl = varchar("foto_url", 300).nullable()
    val seguidores = integer("seguidores").default(0)
    val likesTotales = integer("likes_totales").default(0)
}

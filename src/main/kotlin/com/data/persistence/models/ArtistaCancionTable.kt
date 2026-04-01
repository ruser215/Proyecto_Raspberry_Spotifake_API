package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object ArtistaCancionTable : Table("artista_cancion") {
    val idArtista = reference("id_artista", ArtistaTable)
    val idCancion = reference("id_cancion", CancionTable)
    override val primaryKey = PrimaryKey(idArtista, idCancion)


package com.data.persistence.models
import org.jetbrains.exposed.sql.Table

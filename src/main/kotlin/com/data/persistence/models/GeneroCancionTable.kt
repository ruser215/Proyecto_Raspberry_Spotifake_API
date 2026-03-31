package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object GeneroCancionTable : Table("genero_cancion") {
    val idGenero = reference("id_genero", GeneroTable)
    val idCancion = reference("id_cancion", CancionTable)
    override val primaryKey = PrimaryKey(idGenero, idCancion)
}

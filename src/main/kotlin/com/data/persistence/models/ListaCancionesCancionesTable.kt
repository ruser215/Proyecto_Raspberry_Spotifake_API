package com.data.persistence.models

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ListaCancionesCancionesTable : Table("lista_canciones_canciones") {
    val idCancion = reference("id_cancion", CancionTable, onDelete = ReferenceOption.CASCADE)
    val idLista = reference("id_lista", ListaCancionesTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(idCancion, idLista)
}

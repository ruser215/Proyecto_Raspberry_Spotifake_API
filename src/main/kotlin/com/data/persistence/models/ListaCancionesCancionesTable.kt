package com.data.persistence.models

/**
 * Tabla pivote entre listas y canciones.
 */
import org.jetbrains.exposed.sql.Table

object ListaCancionesCancionesTable : Table("lista_canciones_canciones") {
    val idCancion = reference("id_cancion", CancionTable)
    val idLista = reference("id_lista", ListaCancionesTable)

    override val primaryKey = PrimaryKey(idCancion, idLista)

    init {
        uniqueIndex(idCancion, idLista)
    }
}

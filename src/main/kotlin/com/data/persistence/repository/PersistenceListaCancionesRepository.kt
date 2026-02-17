/**
 * Implementación de repositorio de listas.
 * Incluye validación para evitar canciones duplicadas dentro de la misma lista.
 */
package com.data.persistence.repository

import CancionDao
import CancionTable
import ListaCancionesCancionesTable
import ListaCancionesDao
import ListaCancionesTable
import UsuarioDao
import UsuarioTable
import com.domain.models.Cancion
import com.domain.models.ListaCanciones
import com.domain.repository.ListaCancionesInterface
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import suspendTransaction

class PersistenceListaCancionesRepository : ListaCancionesInterface {
    override suspend fun createLista(lista: ListaCanciones): ListaCanciones? = suspendTransaction {
        val usuarioExists = UsuarioDao.findById(lista.idUsuario) != null
        if (!usuarioExists) return@suspendTransaction null

        ListaCancionesDao.new {
            nombre = lista.nombre
            idUsuario = EntityID(lista.idUsuario, UsuarioTable)
        }.toListaCanciones()
    }

    override suspend fun getListasByUsuario(idUsuario: Long): List<ListaCanciones> = suspendTransaction {
        ListaCancionesDao.find { ListaCancionesTable.idUsuario eq idUsuario }
            .map { it.toListaCanciones() }
    }

    override suspend fun getListaById(idLista: Long): ListaCanciones? = suspendTransaction {
        ListaCancionesDao.findById(idLista)?.toListaCanciones()
    }

    override suspend fun deleteLista(idLista: Long): Boolean = suspendTransaction {
        ListaCancionesCancionesTable.deleteWhere { ListaCancionesCancionesTable.idLista eq idLista }
        val deleted = ListaCancionesTable.deleteWhere { ListaCancionesTable.id eq idLista }
        deleted == 1
    }

    override suspend fun addCancionToLista(idLista: Long, idCancion: Int): Boolean = suspendTransaction {
        val listaExists = ListaCancionesDao.findById(idLista) != null
        val cancionExists = CancionDao.findById(idCancion) != null
        if (!listaExists || !cancionExists) return@suspendTransaction false

        val alreadyExists = ListaCancionesCancionesTable
            .selectAll()
            .where {
                (ListaCancionesCancionesTable.idLista eq idLista) and
                    (ListaCancionesCancionesTable.idCancion eq idCancion)
            }
            .count() > 0

        if (alreadyExists) return@suspendTransaction false

        ListaCancionesCancionesTable.insert {
            it[ListaCancionesCancionesTable.idLista] = EntityID(idLista, ListaCancionesTable)
            it[ListaCancionesCancionesTable.idCancion] = EntityID(idCancion, CancionTable)
        }

        true
    }

    override suspend fun removeCancionFromLista(idLista: Long, idCancion: Int): Boolean = suspendTransaction {
        val deleted = ListaCancionesCancionesTable.deleteWhere {
            (ListaCancionesCancionesTable.idLista eq idLista) and
                (ListaCancionesCancionesTable.idCancion eq idCancion)
        }
        deleted == 1
    }

    override suspend fun getCancionesByLista(idLista: Long): List<Cancion> = suspendTransaction {
        val songIds = ListaCancionesCancionesTable
            .selectAll()
            .where { ListaCancionesCancionesTable.idLista eq idLista }
            .map { it[ListaCancionesCancionesTable.idCancion].value }

        if (songIds.isEmpty()) return@suspendTransaction emptyList()

        CancionDao.find { CancionTable.id inList songIds }
            .map { it.toCancion() }
    }
}

package com.data.persistence.repository

import com.domain.models.Cancion
import com.domain.repository.CancionInterface
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import CancionDao
import CancionTable
import suspendTransaction

class PersistenceCancionRepository : CancionInterface {
    override suspend fun createCancion(cancion: Cancion): Cancion = suspendTransaction {
        CancionDao.new {
            nombre = cancion.nombre
            artista = cancion.artista
            album = cancion.album
            audioUrl = cancion.audioUrl
            portadaUrl = cancion.portadaUrl
        }.toCancion()
    }

    override suspend fun getCancionById(id: Int): Cancion? = suspendTransaction {
        CancionDao.findById(id)?.toCancion()
    }

    override suspend fun getAllCanciones(): List<Cancion> = suspendTransaction {
        CancionDao.all().map { it.toCancion() }
    }

    override suspend fun searchCanciones(nombre: String?, artista: String?, album: String?): List<Cancion> = suspendTransaction {
        var op: Op<Boolean> = Op.TRUE
        if (!nombre.isNullOrBlank()) op = op and (CancionTable.nombre like "%$nombre%")
        if (!artista.isNullOrBlank()) op = op and (CancionTable.artista like "%$artista%")
        if (!album.isNullOrBlank()) op = op and (CancionTable.album like "%$album%")
        CancionDao.find { op }.map { it.toCancion() }
    }

    override suspend fun updateCancion(
        id: Int,
        nombre: String?,
        artista: String?,
        album: String?,
        audioUrl: String?,
        portadaUrl: String?
    ): Cancion? {
        suspendTransaction {
            CancionTable.update({ CancionTable.id eq id }) { stm ->
                nombre?.let { stm[CancionTable.nombre] = it }
                artista?.let { stm[CancionTable.artista] = it }
                album?.let { stm[CancionTable.album] = it }
                audioUrl?.let { stm[CancionTable.audioUrl] = it }
                portadaUrl?.let { stm[CancionTable.portadaUrl] = it }
            }
        }
        return getCancionById(id)
    }

    override suspend fun deleteCancion(id: Int): Cancion? {
        val existing = getCancionById(id) ?: return null
        val deleted = suspendTransaction {
            CancionTable.deleteWhere { CancionTable.id eq id }
        }
        return if (deleted == 1) existing else null
    }
}

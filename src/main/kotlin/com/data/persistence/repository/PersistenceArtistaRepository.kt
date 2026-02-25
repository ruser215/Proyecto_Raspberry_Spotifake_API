package com.data.persistence.repository

import com.domain.models.Artista
import com.domain.repository.ArtistaInterface
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.data.persistence.models.*

class PersistenceArtistaRepository : ArtistaInterface {
    override suspend fun createArtista(artista: Artista): Artista = suspendTransaction {
        ArtistDao.new {
            nombre = artista.nombre
            fotoUrl = artista.fotoUrl
        }.toArtista()
    }

    override suspend fun getArtistaById(id: Int): Artista? = suspendTransaction {
        ArtistDao.findById(id)?.toArtista()
    }

    override suspend fun getAllArtistas(): List<Artista> = suspendTransaction {
        ArtistDao.all().map { it.toArtista() }
    }

    override suspend fun searchArtistas(nombre: String?): List<Artista> = suspendTransaction {
        if (nombre.isNullOrBlank()) return@suspendTransaction ArtistDao.all().map { it.toArtista() }
        ArtistDao.find { ArtistTable.nombre like "%$nombre%" }.map { it.toArtista() }
    }

    override suspend fun updateArtista(id: Int, nombre: String?, fotoUrl: String?): Artista? {
        suspendTransaction {
            ArtistTable.update({ ArtistTable.id eq id }) { stm ->
                nombre?.let { stm[ArtistTable.nombre] = it }
                fotoUrl?.let { stm[ArtistTable.fotoUrl] = it }
            }
        }
        return getArtistaById(id)
    }

    override suspend fun deleteArtista(id: Int): Boolean {
        val deleted = suspendTransaction {
            ArtistTable.deleteWhere { ArtistTable.id eq id }
        }
        return deleted > 0
    }
}

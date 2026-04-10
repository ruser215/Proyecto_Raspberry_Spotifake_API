package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.*
import com.domain.repository.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

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
        ArtistDao.find { ArtistaTable.nombre like "%$nombre%" }.map { it.toArtista() }
    }

    override suspend fun updateArtista(
        id: Int,
        nombre: String?,
        fotoUrl: String?,
        seguidores: Int?,
        likesTotales: Int?
    ): Artista? {
        suspendTransaction {
            ArtistaTable.update({ ArtistaTable.id eq id }) { stm ->
                nombre?.let { stm[ArtistaTable.nombre] = it }
                fotoUrl?.let { stm[ArtistaTable.fotoUrl] = it }
                seguidores?.let { stm[ArtistaTable.seguidores] = it }
                likesTotales?.let { stm[ArtistaTable.likesTotales] = it }
            }
        }
        return getArtistaById(id)
    }

    override suspend fun followArtista(id: Int): Boolean = suspendTransaction {
        val rows = ArtistaTable.update({ ArtistaTable.id eq id }) {
            with(SqlExpressionBuilder) {
                it.update(ArtistaTable.seguidores, ArtistaTable.seguidores + 1)
            }
        }
        rows > 0
    }

    override suspend fun unfollowArtista(id: Int): Boolean = suspendTransaction {
        val rows = ArtistaTable.update({ ArtistaTable.id eq id }) {
            with(SqlExpressionBuilder) {
                it[ArtistaTable.seguidores] = (ArtistaTable.seguidores + (-1)).let { expr ->
                    // Exposed no soporta coerceAtLeast directamente sobre Expression, así que se debe controlar en la lógica
                    // Aquí se asume que el valor nunca será menor que 0 en la base de datos, o se puede hacer un select previo si se requiere
                    expr
                }
            }
        }
        rows > 0
    }

    override suspend fun deleteArtista(id: Int): Boolean {
        val deleted = suspendTransaction {
            ArtistaTable.deleteWhere { ArtistaTable.id eq id }
        }
        return deleted > 0
    }
}

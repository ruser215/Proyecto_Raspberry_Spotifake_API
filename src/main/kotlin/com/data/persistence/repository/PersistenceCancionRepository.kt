/**
 * Implementación de repositorio de canciones.
 * Gestiona altas, filtros y actualizaciones en la tabla de canciones.
 */
package com.data.persistence.repository

import com.domain.models.Cancion
import com.domain.repository.CancionInterface
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.dao.id.EntityID
import CancionDao
import CancionTable
import GeneroTable
import suspendTransaction

// incorporamos nuevas entidades para normalización
import com.data.persistence.models.ArtistDao
import com.data.persistence.models.AlbumDao
import com.data.persistence.models.ArtistTable
import com.data.persistence.models.AlbumTable

class PersistenceCancionRepository : CancionInterface {
    private fun findOrCreateArtist(name: String): ArtistDao = ArtistDao.find {
        ArtistTable.nombre eq name
    }.firstOrNull() ?: ArtistDao.new {
        nombre = name
    }

    private fun findOrCreateAlbum(name: String, artist: ArtistDao): AlbumDao = AlbumDao.find {
        (AlbumTable.nombre eq name) and (AlbumTable.artista eq artist.id)
    }.firstOrNull() ?: AlbumDao.new {
        this.nombre = name
        this.artista = artist
    }

    override suspend fun createCancion(cancion: Cancion): Cancion = suspendTransaction {
        // si el cliente nos manda nombre de artista/álbum convertimos a relaciones
        val artistDao = findOrCreateArtist(cancion.artista)
        val albumDao = findOrCreateAlbum(cancion.album, artistDao)
        CancionDao.new {
            nombre = cancion.nombre
            album = albumDao
            genero = EntityID(cancion.genero, GeneroTable)
            likes = cancion.likes
            urlAudio = cancion.urlAudio
            urlPortada = cancion.urlPortada
        }.toCancion()
    }

    override suspend fun getCancionById(id: Int): Cancion? = suspendTransaction {
        CancionDao.findById(id)?.toCancion()
    }

    override suspend fun getAllCanciones(): List<Cancion> = suspendTransaction {
        CancionDao.all().map { it.toCancion() }
    }

    override suspend fun searchCanciones(nombre: String?, artista: String?, album: String?): List<Cancion> = suspendTransaction {
        // filtrado en memoria usando relaciones para mantener simples las consultas
        CancionDao.all().filter { song ->
            val matchName = nombre.isNullOrBlank() || song.nombre.contains(nombre, ignoreCase = true)
            val matchAlbum = album.isNullOrBlank() || song.album?.nombre?.contains(album, ignoreCase = true) == true
            val matchArtist = artista.isNullOrBlank() || song.album?.artista?.nombre?.contains(artista, ignoreCase = true) == true
            matchName && matchAlbum && matchArtist
        }.map { it.toCancion() }
    }

    override suspend fun updateCancion(
        id: Int,
        nombre: String?,
        artista: String?,
        album: String?,
        genero: Int?,
        likes: Int?,
        urlAudio: String?,
        urlPortada: String?
    ): Cancion? {
        suspendTransaction {
            if (artista != null || album != null) {
                // si cambian las referencias hay que localizar/crear nuevos
                val song = CancionDao.findById(id)
                if (song != null) {
                    val artistDao = if (artista != null) findOrCreateArtist(artista) else song.album?.artista
                    val albumDao = if (album != null && artistDao != null) findOrCreateAlbum(album, artistDao) else song.album
                    if (albumDao != null) song.album = albumDao
                }
            }
            CancionTable.update({ CancionTable.id eq id }) { stm ->
                nombre?.let { stm[CancionTable.nombre] = it }
                genero?.let { stm[CancionTable.genero] = EntityID(it, GeneroTable) }
                likes?.let { stm[CancionTable.likes] = it }
                urlAudio?.let { stm[CancionTable.urlAudio] = it }
                urlPortada?.let { stm[CancionTable.urlPortada] = it }
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

package com.data.persistence.repository

import com.domain.models.Album
import com.domain.repository.AlbumInterface
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.data.persistence.models.*

class PersistenceAlbumRepository : AlbumInterface {
    override suspend fun createAlbum(album: Album): Album = suspendTransaction {
        AlbumDao.new {
            nombre = album.nombre
            portadaUrl = album.portadaUrl
            artista = album.artistaId?.let { ArtistDao.findById(it) } ?: throw IllegalArgumentException("artistaId requerido")
        }.toAlbum()
    }

    override suspend fun getAlbumById(id: Int): Album? = suspendTransaction {
        AlbumDao.findById(id)?.toAlbum()
    }

    override suspend fun getAllAlbums(): List<Album> = suspendTransaction {
        AlbumDao.all().map { it.toAlbum() }
    }

    override suspend fun searchAlbums(nombre: String?, artistaId: Int?): List<Album> = suspendTransaction {
        var seq = AlbumDao.all()
        nombre?.takeIf { it.isNotBlank() }?.let { seq = AlbumDao.find { AlbumTable.nombre like "%$it%" } }
        artistaId?.let { seq = AlbumDao.find { AlbumTable.artista eq it } }
        seq.map { it.toAlbum() }
    }

    override suspend fun getAlbumsByArtista(artistaId: Int): List<Album> = suspendTransaction {
        AlbumDao.find { AlbumTable.artista eq artistaId }.map { it.toAlbum() }
    }

    override suspend fun updateAlbum(id: Int, nombre: String?, portadaUrl: String?, artistaId: Int?): Album? {
        suspendTransaction {
            AlbumTable.update({ AlbumTable.id eq id }) { stm ->
                nombre?.let { stm[AlbumTable.nombre] = it }
                portadaUrl?.let { stm[AlbumTable.portadaUrl] = it }
                artistaId?.let { stm[AlbumTable.artista] = it }
            }
        }
        return getAlbumById(id)
    }

    override suspend fun deleteAlbum(id: Int): Boolean {
        val deleted = suspendTransaction {
            AlbumTable.deleteWhere { AlbumTable.id eq id }
        }
        return deleted > 0
    }
}

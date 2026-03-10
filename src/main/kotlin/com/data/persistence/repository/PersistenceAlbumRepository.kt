package com.data.persistence.repository

import com.domain.models.Album
import com.domain.repository.AlbumInterface
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.data.persistence.models.*

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder

class PersistenceAlbumRepository : AlbumInterface {
    override suspend fun createAlbum(album: Album): Album = suspendTransaction {
        val albumDao = AlbumDao.new {
            nombre = album.nombre
            portadaUrl = album.portadaUrl
            artista = album.artistaId?.let { ArtistDao.findById(it) } ?: throw IllegalArgumentException("artistaId requerido")
        }
        
        // Sincronizar muchos-a-muchos
        album.artistaIds?.forEach { id ->
            ArtistDao.findById(id)?.let { artist ->
                AlbumArtistsTable.insert {
                    it[AlbumArtistsTable.albumId] = albumDao.id
                    it[AlbumArtistsTable.artistId] = artist.id
                }
            }
        }
        
        // Ensure primary artist is included
        album.artistaId?.let { primaryId ->
            if (album.artistaIds?.contains(primaryId) != true) {
                AlbumArtistsTable.insertIgnore {
                    it[AlbumArtistsTable.albumId] = albumDao.id
                    it[AlbumArtistsTable.artistId] = EntityID(primaryId, ArtistTable)
                }
            }
        }
        
        albumDao.toAlbum()
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

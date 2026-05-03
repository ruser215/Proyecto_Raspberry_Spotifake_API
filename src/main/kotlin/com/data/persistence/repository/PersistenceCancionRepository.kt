package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.*
import com.domain.repository.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PersistenceCancionRepository : CancionInterface {
    private fun findOrCreateArtist(name: String): ArtistDao = ArtistDao.find {
        ArtistaTable.nombre eq name
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
        // Usar el primer artistaId de la lista si existe
        val artistaId = cancion.artistaIds.firstOrNull()
        val artistDao = if (artistaId != null) ArtistDao.findById(artistaId)
            else cancion.artista?.let { findOrCreateArtist(it) }

        val albumDao = if (cancion.albumId != null) AlbumDao.findById(cancion.albumId)
                   else if (cancion.album != null && artistDao != null) findOrCreateAlbum(cancion.album, artistDao)
                   else null

        val song = CancionDao.new {
            nombre = cancion.nombre
            artista = artistDao
            album = albumDao
            // genero: usar el primero de generosIds, o el campo genero (informativo)
            val generoId = cancion.generosIds.firstOrNull() ?: cancion.genero
            genero = EntityID(generoId ?: 0, GeneroTable)
            likes = cancion.likes
            reproducciones = cancion.reproducciones
            urlAudio = cancion.urlAudio
            urlPortada = cancion.urlPortada ?: albumDao?.portadaUrl
        }

        song.toCancion()
    }

    override suspend fun getCancionById(id: Int): Cancion? = suspendTransaction {
        CancionDao.findById(id)?.toCancion()
    }

    override suspend fun getAllCanciones(): List<Cancion> = suspendTransaction {
        CancionDao.all().map { it.toCancion() }
    }

    override suspend fun searchCanciones(nombre: String?, artista: String?, album: String?, generoId: Int?): List<Cancion> = suspendTransaction {
        CancionDao.all().filter { song ->
            val matchName = nombre.isNullOrBlank() || song.nombre.contains(nombre, ignoreCase = true)
            val matchAlbum = album.isNullOrBlank() || song.album?.nombre?.contains(album, ignoreCase = true) == true
            val artistName = song.artista?.nombre ?: song.album?.artista?.nombre ?: ""
            val matchArtist = artista.isNullOrBlank() || artistName.contains(artista, ignoreCase = true)
            val matchGenero = generoId == null || song.genero.value == generoId
            matchName && matchAlbum && matchArtist && matchGenero
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
        urlPortada: String?,
        artistaId: Int?,
        albumId: Int?,
        artistaIds: List<Int>?,
        albumIds: List<Int>?,
        generosIds: List<Int>?
    ): Cancion? {
        suspendTransaction {
            val song = CancionDao.findById(id)
            if (song != null) {
                nombre?.let { song.nombre = it }
                genero?.let { song.genero = EntityID(it, GeneroTable) }
                // Usar generosIds si se proporciona (primer elemento), sino genero directo
                generosIds?.firstOrNull()?.let { song.genero = EntityID(it, GeneroTable) }
                likes?.let { song.likes = it }
                urlAudio?.let { song.urlAudio = it }
                urlPortada?.let { song.urlPortada = it }

                // Resolver artista: prioridad artistaIds > artistaId > nombre artista
                val resolvedArtistId = artistaIds?.firstOrNull() ?: artistaId
                val artistDao = if (resolvedArtistId != null) ArtistDao.findById(resolvedArtistId)
                                else if (artista != null) findOrCreateArtist(artista)
                                else null

                if (artistDao != null) song.artista = artistDao

                // Resolver album: prioridad albumIds > albumId > nombre album
                val resolvedAlbumId = albumIds?.firstOrNull() ?: albumId
                val albumDao = if (resolvedAlbumId != null) AlbumDao.findById(resolvedAlbumId)
                               else if (album != null && (artistDao ?: song.artista) != null)
                                   findOrCreateAlbum(album, artistDao ?: song.artista!!)
                               else null

                if (albumDao != null) song.album = albumDao
            }
        }
        return getCancionById(id)
    }

    override suspend fun incrementReproducciones(id: Int): Boolean = suspendTransaction {
        val rows = CancionTable.update({ CancionTable.id eq id }) {
            with(SqlExpressionBuilder) {
                it.update(CancionTable.reproducciones, CancionTable.reproducciones + 1)
            }
        }
        rows > 0
    }

    override suspend fun deleteCancion(id: Int): Cancion? {
        val existing = getCancionById(id) ?: return null
        val deleted = suspendTransaction {
            CancionTable.deleteWhere { CancionTable.id eq id }
        }
        return if (deleted == 1) existing else null
    }
}

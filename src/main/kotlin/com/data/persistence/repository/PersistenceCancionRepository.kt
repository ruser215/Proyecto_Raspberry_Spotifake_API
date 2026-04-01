/**
 * Implementación de repositorio de canciones.
 * Gestiona altas, filtros y actualizaciones en la tabla de canciones.
 */

package com.data.persistence.repository


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
        val artistDao = if (cancion.artistaId != null) ArtistDao.findById(cancion.artistaId!!)
                else cancion.artista?.let { findOrCreateArtist(it) }
        val albumDao = if (cancion.albumId != null) AlbumDao.findById(cancion.albumId!!)
                       else if (cancion.album != null && artistDao != null) findOrCreateAlbum(cancion.album!!, artistDao)
                       else null

        val song = CancionDao.new {
            nombre = cancion.nombre
            artista = artistDao
            album = albumDao
            genero = EntityID(cancion.genero, GeneroTable)
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
        albumId: Int?
    ): Cancion? {
        suspendTransaction {
            val song = CancionDao.findById(id)
            if (song != null) {
                nombre?.let { song.nombre = it }
                genero?.let { song.genero = EntityID(it, GeneroTable) }
                likes?.let { song.likes = it }
                urlAudio?.let { song.urlAudio = it }
                urlPortada?.let { song.urlPortada = it }

                val artistDao = if (artistaId != null) ArtistDao.findById(artistaId)
                                else if (artista != null) findOrCreateArtist(artista)
                                else null

                if (artistDao != null) song.artista = artistDao

                val albumDao = if (albumId != null) AlbumDao.findById(albumId)
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

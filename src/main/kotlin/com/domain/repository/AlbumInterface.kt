package com.domain.repository

import com.domain.models.Album

interface AlbumInterface {
    suspend fun createAlbum(album: Album): Album
    suspend fun getAlbumById(id: Int): Album?
    suspend fun getAllAlbums(): List<Album>
    suspend fun searchAlbums(nombre: String?, artistaId: Int?): List<Album>
    suspend fun getAlbumsByArtista(artistaId: Int): List<Album>
    suspend fun updateAlbum(id: Int, nombre: String?, portadaUrl: String?, artistaId: Int?): Album?
    suspend fun deleteAlbum(id: Int): Boolean
}

package com.domain.repository

import com.domain.models.Artista

interface ArtistaInterface {
    suspend fun createArtista(artista: Artista): Artista
    suspend fun getArtistaById(id: Int): Artista?
    suspend fun getAllArtistas(): List<Artista>
    suspend fun searchArtistas(nombre: String?): List<Artista>
    suspend fun updateArtista(
        id: Int,
        nombre: String? = null,
        fotoUrl: String? = null,
        seguidores: Int? = null,
        likesTotales: Int? = null
    ): Artista?
    suspend fun followArtista(id: Int): Boolean
    suspend fun unfollowArtista(id: Int): Boolean
    suspend fun deleteArtista(id: Int): Boolean
}

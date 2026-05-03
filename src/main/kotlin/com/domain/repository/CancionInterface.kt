/**
 * Contrato de repositorio de canciones.
 * Separa las reglas de dominio del detalle de persistencia.
 */
package com.domain.repository

import com.domain.models.Cancion

interface CancionInterface {
    suspend fun createCancion(cancion: Cancion): Cancion
    suspend fun getCancionById(id: Int): Cancion?
    suspend fun getAllCanciones(): List<Cancion>
    suspend fun searchCanciones(nombre: String?, artista: String?, album: String?, generoId: Int? = null): List<Cancion>
    suspend fun updateCancion(
        id: Int, 
        nombre: String? = null, 
        artista: String? = null, 
        album: String? = null, 
        genero: Int? = null, 
        likes: Int? = null, 
        urlAudio: String? = null, 
        urlPortada: String? = null, 
        artistaId: Int? = null, 
        albumId: Int? = null,
        artistaIds: List<Int>? = null,
        albumIds: List<Int>? = null,
        generosIds: List<Int>? = null
    ): Cancion?
    suspend fun incrementReproducciones(id: Int): Boolean
    suspend fun deleteCancion(id: Int): Cancion?
}

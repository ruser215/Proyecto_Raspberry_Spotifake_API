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
    suspend fun searchCanciones(nombre: String?, artista: String?, album: String?): List<Cancion>
    suspend fun updateCancion(id: Int, nombre: String?, artista: String?, album: String?, genero: Int?, likes: Int?, urlAudio: String?, urlPortada: String?, artistaId: Int? = null, albumId: Int? = null): Cancion?
    suspend fun deleteCancion(id: Int): Cancion?
}

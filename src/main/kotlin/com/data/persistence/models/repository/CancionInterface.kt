package com.domain.repository

import com.domain.models.Cancion

interface CancionInterface {
    suspend fun createCancion(cancion: Cancion): Cancion
    suspend fun getCancionById(id: Int): Cancion?
    suspend fun getAllCanciones(): List<Cancion>
    suspend fun searchCanciones(nombre: String?, artista: String?, album: String?): List<Cancion>
    suspend fun updateCancion(id: Int, nombre: String?, artista: String?, album: String?, audioUrl: String?, portadaUrl: String?): Cancion?
    suspend fun deleteCancion(id: Int): Cancion?
}

package com.domain.repository

import com.domain.models.Letra
import com.domain.models.LetraSync

interface LetraInterface {
    suspend fun getLetraByCancion(cancionId: Int): Letra?
    suspend fun saveLetra(cancionId: Int, textoCompleto: String?, lineas: List<LetraSync>): Letra
}

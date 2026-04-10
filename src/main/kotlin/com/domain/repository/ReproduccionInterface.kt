package com.domain.repository

import com.domain.models.Reproduccion
import java.time.LocalDate

interface ReproduccionInterface {
    suspend fun registerReproduccion(reproduccion: Reproduccion): Reproduccion
    suspend fun getHistoryByUser(userId: Long): List<Reproduccion>
    suspend fun getStatsByUser(userId: Long, year: Int? = null): Map<String, Any> 
}

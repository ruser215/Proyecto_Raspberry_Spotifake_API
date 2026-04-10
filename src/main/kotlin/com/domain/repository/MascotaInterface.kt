package com.domain.repository

import com.domain.models.Mascota

interface MascotaInterface {
    suspend fun getAllMascotas(): List<Mascota>
    suspend fun getMascotasByUser(userId: Long): List<Mascota>
    suspend fun buyMascota(userId: Long, mascotaId: Int): Boolean
    suspend fun setActiveMascota(userId: Long, mascotaId: Int?): Boolean
    suspend fun getActiveMascota(userId: Long): Mascota?
}

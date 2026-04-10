package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.Reproduccion
import com.domain.repository.ReproduccionInterface
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDate

class PersistenceReproduccionRepository : ReproduccionInterface {
    override suspend fun registerReproduccion(reproduccion: Reproduccion): Reproduccion = suspendTransaction {
        val user = UsuarioDao.findById(reproduccion.idUsuario) ?: throw IllegalArgumentException("Usuario no encontrado")
        val song = CancionDao.findById(reproduccion.idCancion) ?: throw IllegalArgumentException("Cancion no encontrada")
        
        ReproduccionDao.new {
            this.usuario = user
            this.cancion = song
            this.fecha = reproduccion.fecha
            this.segundosEscuchados = reproduccion.segundosEscuchados
        }.toReproduccion()
    }

    override suspend fun getHistoryByUser(userId: Long): List<Reproduccion> = suspendTransaction {
        ReproduccionDao.find { ReproduccionTable.idUsuario eq userId }
            .sortedByDescending { it.fecha }
            .map { it.toReproduccion() }
    }

    override suspend fun getStatsByUser(userId: Long, year: Int?): Map<String, Any> = suspendTransaction {
        val allreps = ReproduccionDao.find { ReproduccionTable.idUsuario eq userId }.toList()
        val filtered = if (year != null) allreps.filter { it.fecha.year == year } else allreps
        
        val totalTime = filtered.sumOf { it.segundosEscuchados }
        val topSongs = filtered.groupBy { it.cancion.id.value }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            
        val topArtists = filtered.groupBy { it.cancion.artista?.id?.value }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)

        mapOf(
            "totalTimeSeconds" to totalTime,
            "topSongIds" to topSongs.map { it.first },
            "topArtistIds" to topArtists.map { it.first },
            "totalReproductions" to filtered.size
        )
    }
}

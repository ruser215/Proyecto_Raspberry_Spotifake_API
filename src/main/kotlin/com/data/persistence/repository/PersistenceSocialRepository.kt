package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.SolicitudAmistad
import com.domain.repository.SocialInterface
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.and
import java.time.LocalDate

class PersistenceSocialRepository : SocialInterface {
    
    override suspend fun likeCancion(userId: Long, cancionId: Int): Boolean = suspendTransaction {
        val alreadyLiked = LikeCancionTable.selectAll().where { 
            (LikeCancionTable.idUsuario eq userId) and (LikeCancionTable.idCancion eq cancionId) 
        }.count() > 0
        
        if (alreadyLiked) return@suspendTransaction false
        
        LikeCancionTable.insert {
            it[idUsuario] = userId
            it[idCancion] = cancionId
            it[fecha] = LocalDate.now()
        }
        true
    }

    override suspend fun unlikeCancion(userId: Long, cancionId: Int): Boolean = suspendTransaction {
        val deleted = LikeCancionTable.deleteWhere { 
            (idUsuario eq userId) and (idCancion eq cancionId) 
        }
        deleted > 0
    }

    override suspend fun followArtista(userId: Long, artistaId: Int): Boolean = suspendTransaction {
        val alreadyFollowing = FollowArtistaTable.selectAll().where {
            (FollowArtistaTable.idUsuario eq userId) and (FollowArtistaTable.idArtista eq artistaId)
        }.count() > 0
        
        if (alreadyFollowing) return@suspendTransaction false
        
        FollowArtistaTable.insert {
            it[idUsuario] = userId
            it[idArtista] = artistaId
            it[fecha] = LocalDate.now()
        }
        true
    }

    override suspend fun unfollowArtista(userId: Long, artistaId: Int): Boolean = suspendTransaction {
        val deleted = FollowArtistaTable.deleteWhere {
            (idUsuario eq userId) and (idArtista eq artistaId)
        }
        deleted > 0
    }

    override suspend fun sendFriendRequest(remitenteId: Long, destinatarioId: Long): Boolean = suspendTransaction {
        // Enviar solicitud usando EstadoSolicitudTable
        // asumiendo que idEstado 1 es "PENDIENTE"
        EstadoSolicitudTable.insert {
            it[remitente] = remitenteId
            it[destinatario] = destinatarioId
            it[idEstado] = 1 
            it[fecha] = LocalDate.now()
        }
        true
    }

    override suspend fun acceptFriendRequest(requestId: Int): Boolean = suspendTransaction {
        val request = EstadoSolicitudTable.selectAll().where { EstadoSolicitudTable.id eq requestId }.firstOrNull() ?: return@suspendTransaction false
        val remitente = request[EstadoSolicitudTable.remitente].value
        val destinatario = request[EstadoSolicitudTable.destinatario].value
        
        // Agregar a AmistadTable (ambas direcciones si es simétrico, o solo una si es relación de "amigo")
        AmistadTable.insert {
            it[idUsuario] = remitente
            it[idAmigo] = destinatario
            it[fecha] = LocalDate.now()
        }
        AmistadTable.insert {
            it[idUsuario] = destinatario
            it[idAmigo] = remitente
            it[fecha] = LocalDate.now()
        }
        
        // Actualizar estado a "ACEPTADA" (ej: idEstado 2)
        EstadoSolicitudTable.update({ EstadoSolicitudTable.id eq requestId }) {
            it[idEstado] = 2
        }
        true
    }

    override suspend fun rejectFriendRequest(requestId: Int): Boolean = suspendTransaction {
        // Actualizar estado a "RECHAZADA" (ej: idEstado 3)
        EstadoSolicitudTable.update({ EstadoSolicitudTable.id eq requestId }) {
            it[idEstado] = 3
        }
        true
    }

    override suspend fun getFriends(userId: Long): List<Long> = suspendTransaction {
        AmistadTable.selectAll().where { AmistadTable.idUsuario eq userId }
            .map { it[AmistadTable.idAmigo].value }
    }

    override suspend fun getPendingRequests(userId: Long): List<SolicitudAmistad> = suspendTransaction {
        EstadoSolicitudTable.selectAll().where { (EstadoSolicitudTable.destinatario eq userId) and (EstadoSolicitudTable.idEstado eq 1) }
            .map { 
                SolicitudAmistad(
                    id = it[EstadoSolicitudTable.id].value,
                    remitenteId = it[EstadoSolicitudTable.remitente].value,
                    destinatarioId = it[EstadoSolicitudTable.destinatario].value,
                    estado = "PENDIENTE", // simplistic
                    fecha = it[EstadoSolicitudTable.fecha]
                )
            }
    }
}

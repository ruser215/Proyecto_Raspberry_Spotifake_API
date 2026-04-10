package com.domain.repository

import com.domain.models.SolicitudAmistad

interface SocialInterface {
    suspend fun likeCancion(userId: Long, cancionId: Int): Boolean
    suspend fun unlikeCancion(userId: Long, cancionId: Int): Boolean
    suspend fun followArtista(userId: Long, artistaId: Int): Boolean
    suspend fun unfollowArtista(userId: Long, artistaId: Int): Boolean
    
    suspend fun sendFriendRequest(remitenteId: Long, destinatarioId: Long): Boolean
    suspend fun acceptFriendRequest(requestId: Int): Boolean
    suspend fun rejectFriendRequest(requestId: Int): Boolean
    suspend fun getFriends(userId: Long): List<Long>
    suspend fun getPendingRequests(userId: Long): List<SolicitudAmistad>
}

package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.Letra
import com.domain.models.LetraSync
import com.domain.repository.LetraInterface
import org.jetbrains.exposed.sql.*

class PersistenceLetraRepository : LetraInterface {
    override suspend fun getLetraByCancion(cancionId: Int): Letra? = suspendTransaction {
        val cancionDao = CancionDao.findById(cancionId) ?: return@suspendTransaction null
        LetraDao.find { LetraTable.cancion eq cancionDao.id }.firstOrNull()?.toLetra()
    }

    override suspend fun saveLetra(cancionId: Int, textoCompleto: String?, lineas: List<LetraSync>): Letra = suspendTransaction {
        val cancionDao = CancionDao.findById(cancionId) ?: throw IllegalArgumentException("Cancion no encontrada")
        
        val letraDao = LetraDao.new {
            this.cancion = cancionDao
            this.textoCompleto = textoCompleto
        }

        lineas.forEach { sync ->
            LetraSyncDao.new {
                this.letra = letraDao
                this.timestamp = sync.timestamp
                this.texto = sync.texto
            }
        }

        letraDao.toLetra()
    }
}

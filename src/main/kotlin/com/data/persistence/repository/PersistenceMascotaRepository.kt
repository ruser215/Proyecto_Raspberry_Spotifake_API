package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.Mascota
import com.domain.repository.MascotaInterface
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.and
import java.time.LocalDate

class PersistenceMascotaRepository : MascotaInterface {
    override suspend fun getAllMascotas(): List<Mascota> = suspendTransaction {
        MascotaDao.all().map { it.toMascota() }
    }

    override suspend fun getMascotasByUser(userId: Long): List<Mascota> = suspendTransaction {
        UsuarioMascotaTable.selectAll().where { UsuarioMascotaTable.idUsuario eq userId }
            .mapNotNull { 
                MascotaDao.findById(it[UsuarioMascotaTable.idMascota])?.toMascota()
            }
    }

    override suspend fun buyMascota(userId: Long, mascotaId: Int): Boolean = suspendTransaction {
        val alreadyOwned = UsuarioMascotaTable.selectAll().where {
            (UsuarioMascotaTable.idUsuario eq userId) and (UsuarioMascotaTable.idMascota eq mascotaId)
        }.count() > 0
        
        if (alreadyOwned) return@suspendTransaction false
        
        UsuarioMascotaTable.insert {
            it[idUsuario] = userId
            it[idMascota] = mascotaId
            it[fechaCompra] = LocalDate.now()
            it[activo] = false
        }
        true
    }

    override suspend fun setActiveMascota(userId: Long, mascotaId: Int?): Boolean = suspendTransaction {
        // Desactivar todas
        UsuarioMascotaTable.update({ UsuarioMascotaTable.idUsuario eq userId }) {
            it[activo] = false
        }
        
        if (mascotaId != null) {
            val updated = UsuarioMascotaTable.update({ 
                (UsuarioMascotaTable.idUsuario eq userId) and (UsuarioMascotaTable.idMascota eq mascotaId) 
            }) {
                it[activo] = true
            }
            updated > 0
        } else {
            true
        }
    }

    override suspend fun getActiveMascota(userId: Long): Mascota? = suspendTransaction {
        val activeRelation = UsuarioMascotaTable.selectAll().where {
            (UsuarioMascotaTable.idUsuario eq userId) and (UsuarioMascotaTable.activo eq true)
        }.firstOrNull() ?: return@suspendTransaction null
        
        MascotaDao.findById(activeRelation[UsuarioMascotaTable.idMascota])?.toMascota()
    }
}

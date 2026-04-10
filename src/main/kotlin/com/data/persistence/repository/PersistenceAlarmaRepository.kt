package com.data.persistence.repository

import com.data.persistence.models.*
import com.data.persistence.suspendTransaction
import com.domain.models.Alarma
import com.domain.repository.AlarmaInterface
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PersistenceAlarmaRepository : AlarmaInterface {
    override suspend fun getAlarmsByUser(userId: Long): List<Alarma> = suspendTransaction {
        AlarmaDao.find { AlarmaTable.idUsuario eq userId }.map { it.toAlarma() }
    }

    override suspend fun createAlarm(alarm: Alarma): Alarma = suspendTransaction {
        val user = UsuarioDao.findById(alarm.userId) ?: throw IllegalArgumentException("Usuario no encontrado")
        val song = CancionDao.findById(alarm.cancionId) ?: throw IllegalArgumentException("Cancion no encontrada")
        
        AlarmaDao.new {
            this.usuario = user
            this.nombre = alarm.nombre
            this.hora = alarm.hora
            this.cancion = song
            this.activo = alarm.activo
        }.toAlarma()
    }

    override suspend fun updateAlarm(alarm: Alarma): Boolean = suspendTransaction {
        val dao = AlarmaDao.findById(alarm.id) ?: return@suspendTransaction false
        dao.nombre = alarm.nombre
        dao.hora = alarm.hora
        dao.activo = alarm.activo
        val song = CancionDao.findById(alarm.cancionId)
        if (song != null) dao.cancion = song
        true
    }

    override suspend fun deleteAlarm(alarmId: Int): Boolean = suspendTransaction {
        val dao = AlarmaDao.findById(alarmId) ?: return@suspendTransaction false
        dao.delete()
        true
    }
}

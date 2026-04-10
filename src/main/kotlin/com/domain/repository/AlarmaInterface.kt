package com.domain.repository

import com.domain.models.Alarma

interface AlarmaInterface {
    suspend fun getAlarmsByUser(userId: Long): List<Alarma>
    suspend fun createAlarm(alarm: Alarma): Alarma
    suspend fun updateAlarm(alarm: Alarma): Boolean
    suspend fun deleteAlarm(alarmId: Int): Boolean
}

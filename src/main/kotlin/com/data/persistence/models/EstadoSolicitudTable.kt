package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object EstadoSolicitudTable : IntIdTable("estado_solicitud") {
    val remitente = reference("remitente_id", UsuarioTable)
    val destinatario = reference("destinatario_id", UsuarioTable)
    val idEstado = reference("id_estado", EstadoNotificacionTable)
    val fecha = date("fecha")
    val activo = bool("activo").default(true)
}

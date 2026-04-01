package com.data.persistence.models

package com.data.persistence.models
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object EstadoSolicitudTable : IntIdTable("estado_solicitud") {
    val remitenteId = integer("remitente_id")
    val destinatarioId = integer("destinatario_id")
    val idEstado = reference("id_estado", EstadoNotificacionTable)
    val fecha = date("fecha")
    val activo = bool("activo").default(true)
}

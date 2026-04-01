package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object EstadoNotificacionTable : IntIdTable("estado_notificacion") {
    val estado = varchar("estado", 150)


package com.data.persistence.models
import org.jetbrains.exposed.dao.id.IntIdTable

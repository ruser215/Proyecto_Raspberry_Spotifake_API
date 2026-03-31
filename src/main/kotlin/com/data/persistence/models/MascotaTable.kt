package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object MascotaTable : IntIdTable("mascota") {
    val nombre = varchar("nombre", 200)
    val precio = decimal("precio", 10, 2)
    val urlSprite = varchar("url_sprite", 300)
    val activo = bool("activo").default(true)
    val premiumDefault = bool("premiumdefault").default(false)
    val tipo = reference("id_tipo_animal", TipoMascotaTable)
}

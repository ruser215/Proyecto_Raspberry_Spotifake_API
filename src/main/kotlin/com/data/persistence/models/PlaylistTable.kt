package com.data.persistence.models

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object PlaylistTable : LongIdTable("playlist") {
    val nombre = varchar("nombre", 100)
    val idUsuario = reference("id_usuario", UsuarioTable, onDelete = ReferenceOption.CASCADE)
    val publica = bool("publica").default(false)
    val fecha = date("fecha")
}

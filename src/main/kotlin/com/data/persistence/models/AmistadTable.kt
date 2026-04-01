
package com.data.persistence.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object AmistadTable : Table("amistad") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val idAmigo = reference("id_amigo", UsuarioTable)
    val fecha = date("fecha")
    override val primaryKey = PrimaryKey(idUsuario, idAmigo)
}

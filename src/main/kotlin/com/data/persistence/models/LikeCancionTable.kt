
package com.data.persistence.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object LikeCancionTable : Table("like_cancion") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val idCancion = reference("id_cancion", CancionTable)
    val fecha = date("fecha")
    override val primaryKey = PrimaryKey(idUsuario, idCancion)
}

package com.data.persistence.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object UsuarioMascotaTable : Table("usuario_mascota") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val idMascota = reference("id_mascota", MascotaTable)
    val fechaCompra = date("fecha_compra")
    val activo = bool("activo").default(true)
    override val primaryKey = PrimaryKey(idUsuario, idMascota)
}

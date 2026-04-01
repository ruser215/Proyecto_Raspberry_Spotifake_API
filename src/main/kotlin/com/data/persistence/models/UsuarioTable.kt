
package com.data.persistence.models

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date

object UsuarioTable : LongIdTable("usuario") {
    val username = varchar("username", 200).uniqueIndex()
    val correo = varchar("correo", 300).uniqueIndex()
    val admin = bool("admin").default(false)
    val premium = bool("premium").default(false)
    val pass = varchar("pass", 60)
    val token = varchar("token", 512).nullable()
    val urlImagen = varchar("urlImagen", 255).nullable()
    val fechaRegistro = date("fecha_registro")
}

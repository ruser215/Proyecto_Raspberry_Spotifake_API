package com.data.persistence.models

/**
 * Tabla de usuarios en base de datos.
 */
import org.jetbrains.exposed.dao.id.LongIdTable

object UsuarioTable : LongIdTable("usuarios") {
    val username = varchar("username", 25)
    val correo = varchar("correo", 50)
    val admin = bool("admin").default(false)
    val premium = bool("premium").default(false)
    val pass = varchar("pass", 60)
    val token = varchar("token", 255).nullable()
    val urlImagen = varchar("urlimagen", 255).nullable()
}
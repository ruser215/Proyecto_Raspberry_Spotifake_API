package com.data.persistence.models

/**
 * DAO de usuario con Exposed.
 */
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Usuario

class UsuarioDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UsuarioDao>(UsuarioTable)

    var username by UsuarioTable.username
    var correo by UsuarioTable.correo
    var admin by UsuarioTable.admin
    var premium by UsuarioTable.premium
    var pass by UsuarioTable.pass
    var token by UsuarioTable.token
    var urlImagen by UsuarioTable.urlImagen
    
    
    fun toUsuario(): Usuario {
        return Usuario(
            id = this.id.value,
            username = this.username,
            correo = this.correo,
            admin = this.admin,
            premium = this.premium,
            pass = this.pass,
            token = this.token,
            urlImagen = this.urlImagen
        )
    }
}
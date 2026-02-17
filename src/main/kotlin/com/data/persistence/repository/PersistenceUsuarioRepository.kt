/**
 * Implementación de repositorio de usuarios con Exposed.
 * Traduce operaciones de dominio en consultas SQL transaccionales.
 */
package com.data.persistence.repository

import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.repository.UsuarioInterface
import com.domain.security.PasswordHash
import UsuarioDao
import UsuarioTable
import suspendTransaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class PersistenceUsuarioRepository : UsuarioInterface {

    
    override suspend fun getAllUsuarios(): List<Usuario> {
        return suspendTransaction {
            UsuarioDao.all().map { it.toUsuario() }
        }
    }

    
    override suspend fun getUsuarioByCorreo(correo: String): Usuario? {
        return suspendTransaction {
            UsuarioDao.find {
                UsuarioTable.correo eq correo
            }.firstOrNull()?.toUsuario()
        }
    }

    
    override suspend fun getUsuarioByNombre(nombre: String): List<Usuario> {
        return suspendTransaction {
            UsuarioDao.find {
                UsuarioTable.nombre eq nombre
            }.map { it.toUsuario() }
        }
    }

    
    override suspend fun getUsuarioById(id: Long): Usuario? {
        return suspendTransaction {
            UsuarioDao.findById(id)?.toUsuario()
        }
    }

    
    override suspend fun postUsuario(usuario: Usuario): Usuario? = suspendTransaction {
        UsuarioDao.new {
            this.nombre = usuario.nombre
            this.apellido1 = usuario.apellido1
            this.apellido2 = usuario.apellido2
            this.correo = usuario.correo
            this.admin = usuario.admin
            this.premium = usuario.premium
            this.pass = PasswordHash.hash(usuario.pass)
            this.token = usuario.token
            this.urlImagen = usuario.urlImagen
        }.toUsuario()
    }

    
    override suspend fun register(usuario: UpdateUsuario): Usuario? = suspendTransaction {
        UsuarioDao.new {
            this.nombre = usuario.nombre ?: "Sin nombre"
            this.apellido1 = usuario.apellido1 ?: ""
            this.apellido2 = usuario.apellido2 ?: ""
            this.correo = usuario.correo ?: throw IllegalArgumentException("El correo es obligatorio")
            this.admin = usuario.admin ?: false
            this.premium = usuario.premium ?: false
            this.pass = PasswordHash.hash(usuario.pass ?: throw IllegalArgumentException("La contraseña es obligatoria"))
            this.token = usuario.token ?: ""
            this.urlImagen = usuario.urlImagen
        }.toUsuario()
    }

    
    override suspend fun updateUsuario(updateUsuario: UpdateUsuario, id: Long): Usuario? {
        try {
            suspendTransaction {
                UsuarioTable.update({ UsuarioTable.id eq id }) { stm ->
                    updateUsuario.nombre?.let { stm[nombre] = it }
                    updateUsuario.apellido1?.let { stm[apellido1] = it }
                    updateUsuario.apellido2?.let { stm[apellido2] = it }
                    updateUsuario.correo?.let { stm[correo] = it }
                    updateUsuario.admin?.let { stm[admin] = it }
                    updateUsuario.premium?.let { stm[premium] = it }
                    updateUsuario.pass?.let { stm[pass] = PasswordHash.hash(it) }
                    updateUsuario.token?.let { stm[token] = it }
                    updateUsuario.urlImagen?.let { stm[urlImagen] = it }
                }
            }
            return getUsuarioById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    
    override suspend fun deleteUsuario(id: Long): Boolean = suspendTransaction {
        val num = UsuarioTable.deleteWhere { UsuarioTable.id eq id }
        num == 1
    }

    
    override suspend fun login(correo: String, pass: String): Usuario? {
        val usuario = suspendTransaction {
            UsuarioDao.find { UsuarioTable.correo eq correo }.firstOrNull()?.toUsuario()
        } ?: return null

        return try {
            if (PasswordHash.check(pass, usuario.pass))
                usuario
            else
                null
        } catch (e: Exception) {
            println("Error en la autenticación: ${e.localizedMessage}")
            null
        }
    }
}

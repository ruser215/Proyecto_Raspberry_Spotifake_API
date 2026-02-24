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

    
    override suspend fun getUsuarioByUsername(username: String): List<Usuario> {
        return suspendTransaction {
            UsuarioDao.find {
                UsuarioTable.username eq username
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
            this.username = usuario.username
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
            this.username = usuario.username ?: "Sin nombre"
            this.correo = usuario.correo ?: throw IllegalArgumentException("El correo es obligatorio")
            this.admin = usuario.admin ?: false
            this.premium = usuario.premium ?: false
            this.pass = PasswordHash.hash(usuario.pass ?: throw IllegalArgumentException("La contraseña es obligatoria"))
            this.token = usuario.token
            this.urlImagen = usuario.urlImagen
        }.toUsuario()
    }

    
    override suspend fun updateUsuario(usuario: UpdateUsuario, id: Long): Usuario? {
        try {
            suspendTransaction {
                UsuarioTable.update({ UsuarioTable.id eq id }) { stm ->
                    usuario.username?.let { stm[username] = it }
                    usuario.correo?.let { stm[correo] = it }
                    usuario.admin?.let { stm[admin] = it }
                    usuario.premium?.let { stm[premium] = it }
                    usuario.pass?.let { stm[pass] = PasswordHash.hash(it) }
                    usuario.token?.let { stm[token] = it }
                    usuario.urlImagen?.let { stm[urlImagen] = it }
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

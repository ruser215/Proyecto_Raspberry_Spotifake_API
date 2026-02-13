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

/**
 * Implementación del repositorio de usuarios usando Exposed ORM.
 */
class PersistenceUsuarioRepository : UsuarioInterface {

    /*
    getAllUsuarios():
    Mapea todos los registros de la tabla usuarios a objetos de negocio Usuario.
     */
    override suspend fun getAllUsuarios(): List<Usuario> {
        return suspendTransaction {
            UsuarioDao.all().map { it.toUsuario() }
        }
    }

    /*
    getUsuarioByNickname:
    Busca un usuario por su apodo (nickname).
     */
    override suspend fun getUsuarioByNickname(nickname: String): Usuario? {
        return suspendTransaction {
            UsuarioDao.find {
                UsuarioTable.nickname eq nickname
            }.firstOrNull()?.toUsuario()
        }
    }

    /*
    getUsuarioByNombre:
    Busca usuarios por su nombre de pila.
     */
    override suspend fun getUsuarioByNombre(nombre: String): List<Usuario> {
        return suspendTransaction {
            UsuarioDao.find {
                UsuarioTable.nombre eq nombre
            }.map { it.toUsuario() }
        }
    }

    /*
    getUsuarioById:
    Busca un usuario por su ID único.
     */
    override suspend fun getUsuarioById(id: Int): Usuario? {
        return suspendTransaction {
            UsuarioDao.findById(id)?.toUsuario()
        }
    }

    /*
    postUsuario:
    Crea un nuevo usuario en la base de datos.
     */
    override suspend fun postUsuario(usuario: Usuario): Usuario? = suspendTransaction {
        UsuarioDao.new {
            this.nombre = usuario.nombre
            this.apellido1 = usuario.apellido1
            this.apellido2 = usuario.apellido2
            this.nickname = usuario.nickname
            // Hasheamos la contraseña antes de guardar
            this.contrasena = PasswordHash.hash(usuario.contrasena)
        }.toUsuario()
    }

    /*
    register:
    Registra un nuevo usuario usando UpdateUsuario.
     */
    override suspend fun register(usuario: UpdateUsuario): Usuario? = suspendTransaction {
        UsuarioDao.new {
            this.nombre = usuario.nombre ?: "Sin nombre"
            this.apellido1 = usuario.apellido1 ?: ""
            this.apellido2 = usuario.apellido2 ?: ""
            this.nickname = usuario.nickname ?: throw IllegalArgumentException("El nickname es obligatorio")
            this.contrasena = PasswordHash.hash(usuario.contrasena ?: throw IllegalArgumentException("La contraseña es obligatoria"))
        }.toUsuario()
    }

    /*
    updateUsuario:
    Actualiza los campos permitidos de un usuario existente buscando por ID.
     */
    override suspend fun updateUsuario(updateUsuario: UpdateUsuario, id: Int): Usuario? {
        try {
            suspendTransaction {
                UsuarioTable.update({ UsuarioTable.id eq id }) { stm ->
                    updateUsuario.nombre?.let { stm[nombre] = it }
                    updateUsuario.apellido1?.let { stm[apellido1] = it }
                    updateUsuario.apellido2?.let { stm[apellido2] = it }
                    updateUsuario.nickname?.let { stm[nickname] = it }
                    updateUsuario.contrasena?.let { stm[contrasena] = PasswordHash.hash(it) }
                }
            }
            return getUsuarioById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /*
    deleteUsuario:
    Elimina un usuario por su ID.
     */
    override suspend fun deleteUsuario(id: Int): Boolean = suspendTransaction {
        val num = UsuarioTable.deleteWhere { UsuarioTable.id eq id }
        num == 1
    }

    /*
    login:
    Verifica las credenciales usando el nickname y la contraseña.
     */
    override suspend fun login(nickname: String, pass: String): Usuario? {
        val usuario = suspendTransaction {
            UsuarioDao.find { UsuarioTable.nickname eq nickname }.firstOrNull()?.toUsuario()
        } ?: return null

        return try {
            // Comprobamos si el hash coincide
            if (PasswordHash.check(pass, usuario.contrasena)) 
                usuario
            else
                null
        } catch (e: Exception) {
            println("Error en la autenticación: ${e.localizedMessage}")
            null
        }
    }
}

/**
 * Contrato de repositorio de usuarios.
 * Define operaciones CRUD y autenticación de la capa de dominio.
 */
package com.domain.repository

import com.domain.models.Usuario
import com.domain.models.UpdateUsuario

interface UsuarioInterface {
    
    suspend fun getAllUsuarios(): List<Usuario>

    suspend fun getUsuarioByUsername(username: String): List<Usuario>

    suspend fun getUsuarioByCorreo(correo: String): Usuario?

    suspend fun getUsuarioById(id: Int): Usuario?

    suspend fun postUsuario(usuario: Usuario): Usuario?

    suspend fun updateUsuario(usuario: UpdateUsuario, id: Int): Usuario?

    suspend fun deleteUsuario(id: Int): Boolean

    suspend fun login(correo: String, pass: String): Usuario?

    suspend fun register(usuario: UpdateUsuario): Usuario?

    suspend fun searchUsuarios(query: String): List<Usuario>
}

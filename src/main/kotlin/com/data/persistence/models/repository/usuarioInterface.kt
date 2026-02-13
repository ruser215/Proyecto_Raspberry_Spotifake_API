package com.domain.repository

import com.domain.models.Usuario
import com.domain.models.UpdateUsuario

interface UsuarioInterface {
    
    suspend fun getAllUsuarios(): List<Usuario>

    suspend fun getUsuarioByNombre(nombre: String): List<Usuario>

    suspend fun getUsuarioByNickname(nickname: String): Usuario?

    suspend fun getUsuarioById(id: Int): Usuario?

    suspend fun postUsuario(usuario: Usuario): Usuario?

    suspend fun updateUsuario(usuario: UpdateUsuario, id: Int): Usuario?

    suspend fun deleteUsuario(id: Int): Boolean

    suspend fun login(nickname: String, pass: String): Usuario?

    suspend fun register(usuario: UpdateUsuario): Usuario?
}
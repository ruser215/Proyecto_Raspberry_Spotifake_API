/**
 * Caso de uso de inicio de sesión.
 * Valida credenciales y construye la respuesta de autenticación del usuario.
 */
package com.domain.usecase

import com.domain.models.Usuario
import com.domain.repository.UsuarioInterface



class LoginUseCase(val repository: UsuarioInterface) {
    suspend operator fun invoke(correo: String?, pass: String?): Usuario? {
        if (correo.isNullOrBlank() || pass.isNullOrBlank()) return null

        return try {
            val usuario = repository.login(correo, pass) ?: return null
            usuario.copy(pass = "")
            
        } catch (e: Exception) {
            println("Error en login: ${e.localizedMessage}")
            null
        }
    }
}
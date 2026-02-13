package com.domain.usecase

import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.repository.UsuarioInterface
import com.domain.security.JwtConfig

/*
    Zona de Login y Token.
    1.- Validamos credenciales.
    2.- Generamos un token JWT basado en el nickname o ID.
    3.- Retornamos el usuario con su token.
*/

class LoginUseCase(val repository: UsuarioInterface) {
    suspend operator fun invoke(nickname: String?, pass: String?): Usuario? {
        if (nickname.isNullOrBlank() || pass.isNullOrBlank()) return null

        return try {
            // 1. Intentamos el login en el repositorio
            val usuario = repository.login(nickname, pass) ?: return null

            // 2. Generamos el token JWT (usamos el nickname como identificador)
            val tokenGenerado = JwtConfig.generateToken(usuario.nickname)

            // 3. Preparamos la actualización (si decides guardar el token o actualizar datos)
            // Nota: Si tu tabla no tiene campo 'token', esta parte solo servirá 
            // para devolver el objeto con el token al cliente.
            
            val updateUsuario = UpdateUsuario(
                nickname = usuario.nickname,
                // Si añades el campo 'msg' o 'token' a UpdateUsuario, lo seteamos aquí
                msg = tokenGenerado 
            )

            // 4. Actualización en repositorio (buscando por el ID del usuario encontrado)
            val res = repository.updateUsuario(updateUsuario, usuario.id!!)

            if (res != null) {
                // Devolvemos el usuario. Puedes usar el 'msg' para transportar el token temporalmente
                res.copy(contrasena = "") // Seguridad: Nunca devuelvas la contraseña
            } else {
                null
            }
            
        } catch (e: Exception) {
            println("Error en login: ${e.localizedMessage}")
            null
        }
    }
}
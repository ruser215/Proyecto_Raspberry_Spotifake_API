package com.domain.usecase

import com.domain.infraestructure.Utils
import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.repository.UsuarioInterface
import com.domain.usecase.ProviderUseCase.logger

class RegisterUseCase(val repository: UsuarioInterface) {
    
    operator suspend fun invoke(usuario: UpdateUsuario): Usuario? {

        // Validaciones mínimas obligatorias para el registro
        val nickname = usuario.nickname ?: throw IllegalArgumentException("El nickname es obligatorio")
        val password = usuario.contrasena ?: throw IllegalArgumentException("La contraseña es obligatoria")
        
        // Valores por defecto para campos opcionales
        usuario.nombre = usuario.nombre ?: "Sin nombre"
        usuario.apellido1 = usuario.apellido1 ?: ""
        usuario.apellido2 = usuario.apellido2 ?: ""

        // Verificamos si el usuario ya existe por su nickname
        return if (repository.login(nickname, password) != null) {
            logger.warn("(RegisterUseCase) --> El usuario ya existe.")
            null
        } else {
            logger.warn("(RegisterUseCase) --> No existe, procediendo a crear directorio y registro")
            
            try {
                // Usamos el nickname como identificador para la carpeta de archivos/imágenes
                val isCreate = Utils.createDir(nickname) 
                
                if (isCreate) {
                    logger.warn("(RegisterUseCase) --> Carpeta para $nickname creada correctamente")
                    // Aquí podrías añadir lógica de tratamiento de imagen si añades el campo urlImage después
                } else {
                    // Si el directorio ya existe, podrías decidir si continuar o lanzar error
                    logger.warn("(RegisterUseCase) --> El directorio ya existía.")
                }

                logger.warn("(RegisterUseCase) --> Insertando usuario en la base de datos")
                val reg = repository.register(usuario)
                reg

            } catch (e: Exception) {
                logger.error("(RegisterUseCase) --> Error en el registro: ${e.message}")
                throw IllegalStateException("No se pudo completar el registro del usuario: ${e.message}")
            }
        }
    }
}
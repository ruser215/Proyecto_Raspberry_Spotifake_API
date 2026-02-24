/**
 * Caso de uso de registro.
 * Contiene validaciones de entrada y flujo de alta de nuevos usuarios.
 */
package com.domain.usecase

import com.domain.infraestructure.Utils
import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.repository.UsuarioInterface
import com.domain.usecase.ProviderUseCase.logger

class RegisterUseCase(val repository: UsuarioInterface) {
    
    operator suspend fun invoke(usuario: UpdateUsuario): Usuario? {

        val correo = usuario.correo ?: throw IllegalArgumentException("El correo es obligatorio")
        usuario.pass ?: throw IllegalArgumentException("La contraseña es obligatoria")
        
        usuario.username = usuario.username ?: "Sin nombre"

        return if (repository.getUsuarioByCorreo(correo) != null) {
            logger.warn("(RegisterUseCase) --> El usuario ya existe.")
            null
        } else {
            logger.warn("(RegisterUseCase) --> No existe, procediendo a crear directorio y registro")
            
            try {
                val isCreate = Utils.createDir(correo)
                
                if (isCreate) {
                    logger.warn("(RegisterUseCase) --> Carpeta para $correo creada correctamente")
                } else {
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
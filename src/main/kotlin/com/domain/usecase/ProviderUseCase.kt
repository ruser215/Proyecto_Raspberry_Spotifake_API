package com.domain.usecase

import com.domain.repository.UsuarioInterface
import org.slf4j.LoggerFactory

/**
 * Proveedor de casos de uso con dependencias compartidas.
 */
object ProviderUseCase {
    
    // Logger compartido para todos los casos de uso
    val logger = LoggerFactory.getLogger("UseCaseLogger")
    
    /**
     * Crea una instancia de RegisterUseCase con el repositorio dado.
     */
    fun provideRegisterUseCase(repository: UsuarioInterface): RegisterUseCase {
        return RegisterUseCase(repository)
    }
    
    /**
     * Crea una instancia de LoginUseCase con el repositorio dado.
     */
    fun provideLoginUseCase(repository: UsuarioInterface): LoginUseCase {
        return LoginUseCase(repository)
    }
}

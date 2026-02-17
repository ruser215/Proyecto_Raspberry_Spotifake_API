/**
 * Fábrica de casos de uso.
 * Centraliza la creación de lógica de negocio para simplificar inyección de dependencias.
 */
package com.domain.usecase

import com.domain.repository.UsuarioInterface
import org.slf4j.LoggerFactory


object ProviderUseCase {
    
    val logger = LoggerFactory.getLogger("UseCaseLogger")
    
    
    fun provideRegisterUseCase(repository: UsuarioInterface): RegisterUseCase {
        return RegisterUseCase(repository)
    }
    
    
    fun provideLoginUseCase(repository: UsuarioInterface): LoginUseCase {
        return LoginUseCase(repository)
    }
}

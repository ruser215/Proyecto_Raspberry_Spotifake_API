/**
 * Funciones auxiliares de infraestructura.
 * Incluye utilidades de sistema de archivos para organizar recursos por usuario.
 */
package com.domain.infraestructure

import java.io.File


object Utils {
    
    private const val BASE_DIR = "uploads/usuarios"
    
    
    fun createDir(userKey: String): Boolean {
        return try {
            val userDir = File("$BASE_DIR/$userKey")
            
            if (userDir.exists()) {
                return true
            }
            
            userDir.mkdirs()
        } catch (e: Exception) {
            println("Error al crear directorio para $userKey: ${e.message}")
            false
        }
    }
    
    
    fun getUserDir(userKey: String): String {
        return "$BASE_DIR/$userKey"
    }
}

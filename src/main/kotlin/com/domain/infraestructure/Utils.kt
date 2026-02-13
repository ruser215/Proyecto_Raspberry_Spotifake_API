package com.domain.infraestructure

import java.io.File

/**
 * Utilidades generales para la aplicación.
 */
object Utils {
    
    // Directorio base donde se almacenarán los archivos de usuarios
    private const val BASE_DIR = "uploads/usuarios"
    
    /**
     * Crea un directorio para almacenar archivos de un usuario.
     * @param nickname Identificador único del usuario
     * @return true si el directorio fue creado o ya existía, false si hubo un error
     */
    fun createDir(nickname: String): Boolean {
        return try {
            val userDir = File("$BASE_DIR/$nickname")
            
            // Si el directorio ya existe, retorna true
            if (userDir.exists()) {
                return true
            }
            
            // Crea el directorio incluyendo directorios padres si no existen
            userDir.mkdirs()
        } catch (e: Exception) {
            println("Error al crear directorio para $nickname: ${e.message}")
            false
        }
    }
    
    /**
     * Obtiene la ruta del directorio de un usuario.
     * @param nickname Identificador único del usuario
     * @return Ruta del directorio del usuario
     */
    fun getUserDir(nickname: String): String {
        return "$BASE_DIR/$nickname"
    }
}

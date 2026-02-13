package com.domain.security

import org.mindrot.jbcrypt.BCrypt

/**
 * Utilidad para hashear y verificar contraseñas usando BCrypt.
 */
object PasswordHash {
    
    /**
     * Hashea una contraseña usando BCrypt con salt automático.
     * @param password Contraseña en texto plano
     * @return Hash BCrypt de la contraseña (60 caracteres)
     */
    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
    
    /**
     * Verifica si una contraseña coincide con su hash.
     * @param password Contraseña en texto plano
     * @param hashedPassword Hash BCrypt almacenado
     * @return true si la contraseña es correcta, false en caso contrario
     */
    fun check(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
}

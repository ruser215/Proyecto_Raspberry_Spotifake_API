/**
 * Utilidad de seguridad para hash y verificación de contraseñas.
 * Implementa buenas prácticas al no almacenar contraseñas en texto plano.
 */
package com.domain.security

import org.mindrot.jbcrypt.BCrypt


object PasswordHash {
    
    
    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
    
    
    fun check(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
}

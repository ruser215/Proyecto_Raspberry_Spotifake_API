package com.domain.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * Configuración y generación de tokens JWT para autenticación.
 */
object JwtConfig {
    
    // Configuración del JWT
    private const val SECRET = "tu_secreto_super_seguro_cambialo_en_produccion"
    private const val ISSUER = "spotifake-api"
    private const val AUDIENCE = "spotifake-users"
    private const val VALIDITY_MS = 86400000L // 24 horas en milisegundos
    
    /**
     * Genera un token JWT para un usuario.
     * @param nickname Identificador único del usuario
     * @return Token JWT como String
     */
    fun generateToken(nickname: String): String {
        return JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("nickname", nickname)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(Algorithm.HMAC256(SECRET))
    }
    
    /**
     * Obtiene el secret usado para firmar tokens.
     * Usado para configurar la autenticación JWT en Ktor.
     */
    fun getSecret(): String = SECRET
    
    /**
     * Obtiene el issuer del token.
     */
    fun getIssuer(): String = ISSUER
    
    /**
     * Obtiene el audience del token.
     */
    fun getAudience(): String = AUDIENCE
}

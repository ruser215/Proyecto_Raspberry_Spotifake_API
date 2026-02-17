/**
 * Configuración de autenticación con JWT.
 * Permite emitir tokens firmados para sesiones stateless en APIs REST.
 */
package com.domain.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*


object JwtConfig {
    
    private const val SECRET = "tu_secreto_super_seguro_cambialo_en_produccion"
    private const val ISSUER = "spotifake-api"
    private const val AUDIENCE = "spotifake-users"
    private const val VALIDITY_MS = 86400000L
    
    
    fun generateToken(correo: String): String {
        return JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("correo", correo)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(Algorithm.HMAC256(SECRET))
    }
    
    
    fun getSecret(): String = SECRET
    
    
    fun getIssuer(): String = ISSUER
    
    
    fun getAudience(): String = AUDIENCE
}

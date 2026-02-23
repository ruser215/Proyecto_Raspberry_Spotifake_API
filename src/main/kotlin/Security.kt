package com.example

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.github.cdimascio.dotenv.dotenv
import com.data.persistence.repository.PersistenceUsuarioRepository

fun Application.configureSecurity() {
    val dotenv = dotenv {
        ignoreIfMissing = true
        directory = "."
    }

    val jwtSecret = dotenv["JWT_SECRET"] ?: "secret"
    val jwtDomain = dotenv["JWT_DOMAIN"] ?: "https://jwt-provider-domain/"
    val jwtAudience = dotenv["JWT_AUDIENCE"] ?: "jwt-audience"
    val jwtRealm = dotenv["JWT_REALM"] ?: "ktor sample app"

    val repository = PersistenceUsuarioRepository()

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    val correo = credential.payload.getClaim("correo").asString()
                    val tokenFromDb = repository.getUsuarioByCorreo(correo)?.token
                    
                    // Verificación extra: El token debe ser el mismo que el de la BD
                    if (tokenFromDb != null && tokenFromDb.isNotEmpty()) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}

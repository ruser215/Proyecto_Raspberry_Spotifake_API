package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import com.data.persistence.repository.PersistenceUsuarioRepository
import com.domain.usecase.ProviderUseCase
import com.domain.models.Usuario
import com.domain.models.UpdateUsuario

fun Application.configureRouting() {
    val repository = PersistenceUsuarioRepository()
    val registerUseCase = ProviderUseCase.provideRegisterUseCase(repository)
    val loginUseCase = ProviderUseCase.provideLoginUseCase(repository)
    
    routing {
        // Endpoint de prueba
        get("/") {
            call.respondText("¡API Spotifake funcionando! 🎵")
        }
        
        // Registro de usuario
        post("/register") {
            try {
                val updateUsuario = call.receive<UpdateUsuario>()
                val usuario = registerUseCase(updateUsuario)
                
                if (usuario != null) {
                    // No devolver la contraseña en la respuesta
                    usuario.contrasena = ""
                    call.respond(HttpStatusCode.Created, usuario)
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("error" to "El usuario ya existe o hubo un error en el registro")
                    )
                }
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to e.message)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error interno del servidor: ${e.message}")
                )
            }
        }
        
        // Login de usuario
        post("/login") {
            try {
                val credentials = call.receive<UpdateUsuario>()
                val usuario = loginUseCase(credentials.nickname, credentials.contrasena)
                
                if (usuario != null) {
                    // El token JWT está en el campo msg
                    usuario.contrasena = "" // No devolver la contraseña
                    call.respond(HttpStatusCode.OK, usuario)
                } else {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "Credenciales inválidas")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error en el login: ${e.message}")
                )
            }
        }
        
        // Obtener todos los usuarios
        get("/usuarios") {
            try {
                val usuarios = repository.getAllUsuarios()
                // No devolver contraseñas
                usuarios.forEach { it.contrasena = "" }
                call.respond(HttpStatusCode.OK, usuarios)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener usuarios: ${e.message}")
                )
            }
        }
        
        // Obtener usuario por ID
        get("/usuarios/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID inválido")
                    )
                    return@get
                }
                
                val usuario = repository.getUsuarioById(id)
                if (usuario != null) {
                    usuario.contrasena = ""
                    call.respond(HttpStatusCode.OK, usuario)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Usuario no encontrado")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al obtener usuario: ${e.message}")
                )
            }
        }
        
        // Buscar usuario por nickname
        get("/usuarios/nickname/{nickname}") {
            try {
                val nickname = call.parameters["nickname"]
                if (nickname.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Nickname inválido")
                    )
                    return@get
                }
                
                val usuario = repository.getUsuarioByNickname(nickname)
                if (usuario != null) {
                    usuario.contrasena = ""
                    call.respond(HttpStatusCode.OK, usuario)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Usuario no encontrado")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al buscar usuario: ${e.message}")
                )
            }
        }
        
        // Actualizar usuario
        put("/usuarios/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID inválido")
                    )
                    return@put
                }
                
                val updateUsuario = call.receive<UpdateUsuario>()
                val usuario = repository.updateUsuario(updateUsuario, id)
                
                if (usuario != null) {
                    usuario.contrasena = ""
                    call.respond(HttpStatusCode.OK, usuario)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Usuario no encontrado o error en la actualización")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al actualizar usuario: ${e.message}")
                )
            }
        }
        
        // Eliminar usuario
        delete("/usuarios/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "ID inválido")
                    )
                    return@delete
                }
                
                val deleted = repository.deleteUsuario(id)
                if (deleted) {
                    call.respond(
                        HttpStatusCode.OK,
                        mapOf("message" to "Usuario eliminado correctamente")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Usuario no encontrado")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar usuario: ${e.message}")
                )
            }
        }
        
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
    }
}

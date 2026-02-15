package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import com.data.persistence.repository.PersistenceUsuarioRepository
import com.data.persistence.repository.PersistenceCancionRepository
import com.domain.usecase.ProviderUseCase
import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.models.Cancion
import java.io.File
import java.util.UUID

fun Application.configureRouting() {
    val repository = PersistenceUsuarioRepository()
    val registerUseCase = ProviderUseCase.provideRegisterUseCase(repository)
    val loginUseCase = ProviderUseCase.provideLoginUseCase(repository)
    val cancionRepository = PersistenceCancionRepository()
    
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

        // Crear canción (multipart/form-data)
        post("/canciones") {
            val multipart = call.receiveMultipart()
            var nombre: String? = null
            var artista: String? = null
            var album: String? = null
            var audioUrl: String? = null
            var portadaUrl: String? = null

            val audioDir = File("archivos/audio").apply { mkdirs() }
            val portadaDir = File("archivos/portadas").apply { mkdirs() }

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "nombre" -> nombre = part.value
                            "artista" -> artista = part.value
                            "album" -> album = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        when (part.name) {
                            "audio" -> audioUrl = saveFile(part, audioDir, "/archivos/audio")
                            "portada" -> portadaUrl = saveFile(part, portadaDir, "/archivos/portadas")
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (nombre.isNullOrBlank() || artista.isNullOrBlank() || album.isNullOrBlank() || audioUrl.isNullOrBlank() || portadaUrl.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Faltan campos obligatorios o archivos"))
                return@post
            }

            val cancion = cancionRepository.createCancion(
                Cancion(
                    nombre = nombre!!,
                    artista = artista!!,
                    album = album!!,
                    audioUrl = audioUrl!!,
                    portadaUrl = portadaUrl!!
                )
            )

            call.respond(HttpStatusCode.Created, cancion)
        }

        // Listar canciones con filtros opcionales
        get("/canciones") {
            val nombre = call.request.queryParameters["nombre"]
            val artista = call.request.queryParameters["artista"]
            val album = call.request.queryParameters["album"]

            val canciones = if (nombre.isNullOrBlank() && artista.isNullOrBlank() && album.isNullOrBlank()) {
                cancionRepository.getAllCanciones()
            } else {
                cancionRepository.searchCanciones(nombre, artista, album)
            }
            call.respond(HttpStatusCode.OK, canciones)
        }

        // Obtener canción por ID
        get("/canciones/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                return@get
            }
            val cancion = cancionRepository.getCancionById(id)
            if (cancion == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Canción no encontrada"))
            } else {
                call.respond(HttpStatusCode.OK, cancion)
            }
        }

        // Actualizar canción (multipart/form-data)
        put("/canciones/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                return@put
            }

            val existing = cancionRepository.getCancionById(id)
            if (existing == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Canción no encontrada"))
                return@put
            }

            val multipart = call.receiveMultipart()
            var nombre: String? = null
            var artista: String? = null
            var album: String? = null
            var newAudioUrl: String? = null
            var newPortadaUrl: String? = null

            val audioDir = File("archivos/audio").apply { mkdirs() }
            val portadaDir = File("archivos/portadas").apply { mkdirs() }

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "nombre" -> nombre = part.value
                            "artista" -> artista = part.value
                            "album" -> album = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        when (part.name) {
                            "audio" -> newAudioUrl = saveFile(part, audioDir, "/archivos/audio")
                            "portada" -> newPortadaUrl = saveFile(part, portadaDir, "/archivos/portadas")
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            val updated = cancionRepository.updateCancion(
                id,
                nombre,
                artista,
                album,
                newAudioUrl,
                newPortadaUrl
            )

            if (updated == null) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar la canción"))
                return@put
            }

            if (!newAudioUrl.isNullOrBlank()) {
                deleteLocalFile(existing.audioUrl)
            }
            if (!newPortadaUrl.isNullOrBlank()) {
                deleteLocalFile(existing.portadaUrl)
            }

            call.respond(HttpStatusCode.OK, updated)
        }

        // Eliminar canción
        delete("/canciones/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                return@delete
            }

            val deleted = cancionRepository.deleteCancion(id)
            if (deleted == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Canción no encontrada"))
                return@delete
            }

            deleteLocalFile(deleted.audioUrl)
            deleteLocalFile(deleted.portadaUrl)

            call.respond(HttpStatusCode.OK, mapOf("message" to "Canción eliminada correctamente"))
        }
        
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
        staticFiles("/archivos", File("archivos"))
    }
}

private fun saveFile(part: PartData.FileItem, dir: File, urlPrefix: String): String {
    val original = part.originalFileName ?: "file"
    val safeName = original.replace("\\s+".toRegex(), "_")
    val fileName = "${UUID.randomUUID()}_${safeName}"
    val target = File(dir, fileName)
    part.streamProvider().use { input ->
        target.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return "$urlPrefix/$fileName"
}

private fun deleteLocalFile(url: String) {
    val relative = url.removePrefix("/")
    val file = File(relative)
    if (file.exists()) {
        file.delete()
    }
}

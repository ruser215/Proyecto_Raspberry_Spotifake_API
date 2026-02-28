/**
 *  Definición de rutas HTTP de la API.
 * Este archivo muestra cómo diseñar endpoints REST en Ktor y cómo conectar
 * la capa web con los repositorios de persistencia.
 */
package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import kotlinx.serialization.json.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import com.data.persistence.repository.*
import com.data.persistence.models.*
import com.domain.usecase.ProviderUseCase
import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.models.Cancion
import com.domain.models.Genero
import com.domain.models.ListaCanciones
import com.domain.models.ListaCancionesCancionRequest
import com.domain.models.Artista
import com.domain.models.Album
import java.io.File
import java.util.UUID
import java.util.Date
import java.net.URLDecoder

fun Application.configureRouting() {
    val repository = PersistenceUsuarioRepository()
    val registerUseCase = ProviderUseCase.provideRegisterUseCase(repository)
    val loginUseCase = ProviderUseCase.provideLoginUseCase(repository)
    val cancionRepository = PersistenceCancionRepository()
    val generoRepository = PersistenceGeneroRepository()
    val listaCancionesRepository = PersistenceListaCancionesRepository()
    // nuevos repositorios normalizados
    val artistaRepository = PersistenceArtistaRepository()
    val albumRepository = PersistenceAlbumRepository()

    val dotenv = dotenv {
        ignoreIfMissing = true
        directory = "."
    }
    val jwtSecret = dotenv["JWT_SECRET"] ?: "secret"
    val jwtDomain = dotenv["JWT_DOMAIN"] ?: "https://jwt-provider-domain/"
    val jwtAudience = dotenv["JWT_AUDIENCE"] ?: "jwt-audience"

    fun generateToken(usuario: Usuario): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withClaim("correo", usuario.correo)
            .withClaim("id", usuario.id)
            .withClaim("admin", usuario.admin)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 24 horas
            .sign(Algorithm.HMAC256(jwtSecret))
    }
    
    routing {
        // Listar todos los archivos QR (protegido, solo dentro de authenticate)

        // --- ENDPOINTS PÚBLICOS ---
        // Descargar archivo APK
        get("/apk/{nombre}") {
            val nombre = call.parameters["nombre"]
            if (nombre.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nombre de archivo requerido"))
                return@get
            }
            val file = File("archivos/apk/$nombre")
            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Archivo no encontrado"))
                return@get
            }
            call.respondFile(file)
        }

        // Descargar imagen QR
        get("/qr/{nombre}") {
            val nombre = call.parameters["nombre"]
            if (nombre.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nombre de archivo requerido"))
                return@get
            }
            val file = File("archivos/qr/$nombre")
            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Archivo no encontrado"))
                return@get
            }
            call.respondFile(file)
        }

        // --- ENDPOINTS PROTEGIDOS ---
        authenticate("auth-jwt") {
                        delete("/usuarios/{id}") {
                            val principal = call.principal<JWTPrincipal>()
                            val isAdmin = principal?.getClaim("admin", Int::class) == 1
                            if (!isAdmin) {
                                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden eliminar usuarios"))
                                return@delete
                            }
                            try {
                                val id = call.parameters["id"]?.toLongOrNull()
                                if (id == null) {
                                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                                    return@delete
                                }
                                val eliminado = repository.deleteUsuario(id)
                                if (eliminado) {
                                    call.respond(HttpStatusCode.OK, mapOf("message" to "Usuario eliminado correctamente"))
                                } else {
                                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuario no encontrado"))
                                }
                            } catch (e: Exception) {
                                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al eliminar usuario: ${e.message}"))
                            }
                        }
            // Subir imagen QR
            post("/qr") {
                val multipart = call.receiveMultipart()
                val qrDir = File("archivos/qr").apply { mkdirs() }
                var urlQR: String? = null
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem && part.name == "qr") {
                        urlQR = saveFile(part, qrDir, "/archivos/qr")
                    }
                    part.dispose()
                }
                if (urlQR != null) {
                    call.respond(HttpStatusCode.Created, mapOf("url" to urlQR))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No se recibió ninguna imagen QR"))
                }
            }

            // Listar imágenes QR
            get("/qr") {
                val qrDir = File("archivos/qr").apply { mkdirs() }
                val archivos = qrDir.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
                call.respond(HttpStatusCode.OK, mapOf("archivos" to archivos))
            }

            // Eliminar imagen QR
            delete("/qr/{nombre}") {
                val nombre = call.parameters["nombre"]
                if (nombre.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nombre de archivo requerido"))
                    return@delete
                }
                val file = File("archivos/qr/$nombre")
                if (!file.exists()) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Archivo no encontrado"))
                    return@delete
                }
                if (file.delete()) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Archivo eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "No se pudo eliminar el archivo"))
                }
            }

            // Subir archivo APK
            post("/apk") {
                val multipart = call.receiveMultipart()
                val apkDir = File("archivos/apk").apply { mkdirs() }
                var urlAPK: String? = null
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem && part.name == "apk") {
                        urlAPK = saveFile(part, apkDir, "/archivos/apk")
                    }
                    part.dispose()
                }
                if (urlAPK != null) {
                    call.respond(HttpStatusCode.Created, mapOf("url" to urlAPK))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No se recibió ningún archivo APK"))
                }
            }

            // Eliminar archivo APK
            delete("/apk/{nombre}") {
                val nombre = call.parameters["nombre"]
                if (nombre.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Nombre de archivo requerido"))
                    return@delete
                }
                val file = File("archivos/apk/$nombre")
                if (!file.exists()) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Archivo no encontrado"))
                    return@delete
                }
                if (file.delete()) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Archivo APK eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "No se pudo eliminar el archivo"))
                }
            }

            // Listar archivos APK
            get("/apk") {
                val apkDir = File("archivos/apk").apply { mkdirs() }
                val archivos = apkDir.listFiles()?.filter { it.isFile }?.map { it.name } ?: emptyList()
                call.respond(HttpStatusCode.OK, mapOf("archivos" to archivos))
            }
        }
        get("/") {
            call.respondText("¡API Spotifake funcionando! ")
        }

        post("/register") {
            try {
                val updateUsuario = call.receive<UpdateUsuario>()
                val usuario = registerUseCase(updateUsuario)
                
                if (usuario != null) {
                    val token = generateToken(usuario)
                    repository.updateUsuario(UpdateUsuario(token = token), usuario.id!!)
                    
                    val usuarioConToken = usuario.copy(token = token)
                    usuarioConToken.pass = ""
                    call.respond(HttpStatusCode.Created, usuarioConToken)
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
        
        post("/login") {
            try {
                val credentials = call.receive<UpdateUsuario>()
                val usuario = loginUseCase(credentials.correo!!, credentials.pass!!)
                
                if (usuario != null) {
                    val token = generateToken(usuario)
                    repository.updateUsuario(UpdateUsuario(token = token), usuario.id!!)
                    
                    val usuarioConToken = usuario.copy(token = token)
                    usuarioConToken.pass = ""
                    call.respond(HttpStatusCode.OK, usuarioConToken)
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

        authenticate("auth-jwt") {
            get("/usuarios") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden listar usuarios"))
                    return@get
                }
                try {
                    val usuarios = repository.getAllUsuarios()
                    usuarios.forEach { it.pass = "" }
                    call.respond(HttpStatusCode.OK, usuarios)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al obtener usuarios: ${e.message}")
                    )
                }
            }
            
            get("/usuarios/{id}") {
                try {
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@get
                    }
                    
                    val usuario = repository.getUsuarioById(id)
                    if (usuario != null) {
                        usuario.pass = ""
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
            
            get("/usuarios/correo/{correo}") {
                try {
                    val correo = call.parameters["correo"]
                    if (correo.isNullOrBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Correo inválido")
                        )
                        return@get
                    }
                    
                    val usuario = repository.getUsuarioByCorreo(correo)
                    if (usuario != null) {
                        usuario.pass = ""
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
            
            patch("/usuarios/{id}") {
                try {
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "ID inválido")
                        )
                        return@patch
                    }
                    
                    val updateUsuario = call.receive<UpdateUsuario>()
                    val usuario = repository.updateUsuario(updateUsuario, id)
                    
                    if (usuario != null) {
                        usuario.pass = ""
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


                // Validación de admin debe ir dentro de cada endpoint que lo requiera

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

            patch("/canciones/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden editar canciones"))
                    return@patch
                }
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@patch
                }

                val existing = cancionRepository.getCancionById(id)
                if (existing == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Canción no encontrada"))
                    return@patch
                }

                val multipart = call.receiveMultipart()
                var nombre: String? = null
                var artista: String? = null
                var album: String? = null
                var artistaId: Int? = null
                var albumId: Int? = null
                var genero: Int? = null
                var likes: Int? = null
                var newUrlAudio: String? = null
                var newUrlPortada: String? = null

                val audioDir = File("archivos/audio").apply { mkdirs() }
                val portadaDir = File("archivos/portadas").apply { mkdirs() }

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "nombre" -> nombre = part.value
                                "artista" -> artista = part.value
                                "album" -> album = part.value
                                "artistaId" -> artistaId = part.value.toIntOrNull()
                                "albumId" -> albumId = part.value.toIntOrNull()
                                "genero" -> genero = part.value.toIntOrNull()
                                "likes" -> likes = part.value.toIntOrNull()
                            }
                        }
                        is PartData.FileItem -> {
                            when (part.name) {
                                "audio" -> newUrlAudio = saveFile(part, audioDir, "/archivos/audio")
                                "portada" -> newUrlPortada = saveFile(part, portadaDir, "/archivos/portadas")
                            }
                        }
                        else -> Unit
                }
                part.dispose()
            }

            // --- Validaciones de Integridad para Actualización ---
            if (artistaId != null && artistaRepository.getArtistaById(artistaId!!) == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "El artista con ID $artistaId no existe"))
                return@patch
            }
            if (albumId != null && albumRepository.getAlbumById(albumId!!) == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "El álbum con ID $albumId no existe"))
                return@patch
            }
            if (genero != null && generoRepository.getGeneroById(genero!!) == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "El género con ID $genero no existe"))
                return@patch
            }

                val updated = cancionRepository.updateCancion(
                    id,
                    nombre,
                    artista,
                    album,
                    genero,
                    likes,
                    newUrlAudio,
                    newUrlPortada,
                    artistaId,
                    albumId
                )

                if (updated == null) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar la canción"))
                    return@patch
                }

                if (!newUrlAudio.isNullOrBlank()) {
                    deleteLocalFile(existing.urlAudio)
                }
                if (!newUrlPortada.isNullOrBlank()) {
                    existing.urlPortada?.let { deleteLocalFile(it) }
                }

                call.respond(HttpStatusCode.OK, updated)
            }

            delete("/canciones/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden borrar canciones"))
                    return@delete
                }
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@delete
                    }

                    val existing = cancionRepository.getCancionById(id)
                    if (existing == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Canción no encontrada"))
                        return@delete
                    }

                    val deleted = cancionRepository.deleteCancion(id)
                    if (deleted != null) {
                        // Borrar solo el archivo de audio asociado si existe
                        if (!existing.urlAudio.isNullOrBlank()) deleteLocalFile(existing.urlAudio)
                        // No borrar la portada del álbum
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Canción eliminada correctamente"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Canción no encontrada"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al eliminar canción: ${e.message}"))
                }
            }

            // --- Generos ---
            post("/generos") {
                try {
                    val genero = call.receive<Genero>()
                    if (genero.nombre.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El nombre del género es obligatorio"))
                        return@post
                    }

                    val created = generoRepository.createGenero(genero)
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al crear género: ${e.message}")
                    )
                }
            }

            get("/generos") {
                            get("/generos/{id}") {
                                val id = call.parameters["id"]?.toIntOrNull()
                                if (id == null) {
                                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                                    return@get
                                }
                                try {
                                    val genero = generoRepository.getGeneroById(id)
                                    if (genero == null) {
                                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Género no encontrado"))
                                    } else {
                                        call.respond(HttpStatusCode.OK, genero)
                                    }
                                } catch (e: Exception) {
                                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener género: ${e.message}"))
                                }
                            }
                try {
                    val generos = generoRepository.getAllGeneros()
                    call.respond(HttpStatusCode.OK, generos)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al obtener géneros: ${e.message}")
                    )
                }
            }



            // --- CRUD de artistas ---
            post("/artistas") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden crear artistas"))
                    return@post
                }
                try {
                    val multipart = call.receiveMultipart()
                    var nombre: String? = null
                    var urlFoto: String? = null
                    val artistaDir = File("archivos/artistas").apply { mkdirs() }

                    multipart.forEachPart { part ->
                        if (part is PartData.FormItem && part.name == "nombre") {
                            nombre = part.value
                        } else if (part is PartData.FileItem && part.name == "foto") {
                            urlFoto = saveFile(part, artistaDir, "/archivos/artistas")
                        }
                        part.dispose()
                    }

                    if (nombre.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El nombre es obligatorio"))
                        return@post
                    }

                    val created = artistaRepository.createArtista(Artista(nombre = nombre!!, fotoUrl = urlFoto))
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al crear artista: ${e.message}"))
                }
            }

            get("/artistas") {
                try {
                    val nombre = call.request.queryParameters["nombre"]
                    val artistas = if (nombre.isNullOrBlank()) {
                        artistaRepository.getAllArtistas()
                    } else {
                        artistaRepository.searchArtistas(nombre)
                    }
                    call.respond(HttpStatusCode.OK, artistas)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener artistas: ${e.message}"))
                }
            }

            get("/artistas/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@get
                    }
                    val artista = artistaRepository.getArtistaById(id)
                    if (artista != null) call.respond(HttpStatusCode.OK, artista)
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener artista: ${e.message}"))
                }
            }

            patch("/artistas/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden editar artistas"))
                    return@patch
                }
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@patch
                    }

                    val existing = artistaRepository.getArtistaById(id)
                    if (existing == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                        return@patch
                    }

                    val multipart = call.receiveMultipart()
                    var nombre: String? = null
                    var newUrlFoto: String? = null
                    val artistaDir = File("archivos/artistas").apply { mkdirs() }

                    multipart.forEachPart { part ->
                        if (part is PartData.FormItem && part.name == "nombre") {
                            nombre = part.value
                        } else if (part is PartData.FileItem && part.name == "foto") {
                            newUrlFoto = saveFile(part, artistaDir, "/archivos/artistas")
                        }
                        part.dispose()
                    }

                    val updated = artistaRepository.updateArtista(id, nombre?.takeIf { it.isNotBlank() }, newUrlFoto)
                    if (updated != null) {
                        if (!newUrlFoto.isNullOrBlank() && !existing.fotoUrl.isNullOrBlank()) {
                            deleteLocalFile(existing.fotoUrl)
                        }
                        call.respond(HttpStatusCode.OK, updated)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar artista: ${e.message}"))
                }
            }

            delete("/artistas/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden borrar artistas"))
                    return@delete
                }
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@delete
                    }
                    val ok = artistaRepository.deleteArtista(id)
                    if (ok) call.respond(HttpStatusCode.OK, mapOf("message" to "Artista eliminado"))
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Artista no encontrado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al eliminar artista: ${e.message}"))
                }
            }

            // --- CRUD de álbumes ---
            post("/artistas/{id}/albums") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden crear álbumes"))
                    return@post
                }
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de artista inválido"))
                        return@post
                    }

                    val multipart = call.receiveMultipart()
                    var nombre: String? = null
                    var urlPortada: String? = null
                    val albumDir = File("archivos/albums").apply { mkdirs() }

                    multipart.forEachPart { part ->
                        when(part) {
                            is PartData.FormItem -> {
                                if (part.name == "nombre") nombre = part.value
                            }
                            is PartData.FileItem -> {
                                if (part.name == "portada") {
                                    urlPortada = saveFile(part, albumDir, "/archivos/albums")
                                }
                            }
                            else -> Unit
                        }
                        part.dispose()
                    }

                    if (nombre.isNullOrBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El nombre es obligatorio"))
                        return@post
                    }

                    val created = albumRepository.createAlbum(
                        com.domain.models.Album(
                            nombre = nombre!!,
                            portadaUrl = urlPortada,
                            artistaId = id
                        )
                    )
                    call.respond(HttpStatusCode.Created, created)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al crear álbum: ${e.message}"))
                }
            }

            get("/albums") {
                try {
                    val nombre = call.request.queryParameters["nombre"]
                    val albums = if (nombre.isNullOrBlank()) {
                        albumRepository.getAllAlbums()
                    } else {
                        albumRepository.searchAlbums(nombre, null)
                    }
                    call.respond(HttpStatusCode.OK, albums)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener álbumes: ${e.message}"))
                }
            }

            get("/artistas/{id}/albums") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de artista inválido"))
                        return@get
                    }
                    val albums = albumRepository.getAlbumsByArtista(id)
                    call.respond(HttpStatusCode.OK, albums)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener álbumes: ${e.message}"))
                }
            }

            get("/albums/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@get
                    }
                    val album = albumRepository.getAlbumById(id)
                    if (album != null) call.respond(HttpStatusCode.OK, album)
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener álbum: ${e.message}"))
                }
            }

            get("/albums/{id}/canciones") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@get
                    }
                    val album = albumRepository.getAlbumById(id)
                    if (album == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                        return@get
                    }
                    // utilizamos el nombre y artista para filtrar
                    // Ahora podemos filtrar directamente por álbum ID si quisiéramos, 
                    // pero mantenemos searchCanciones para compatibilidad si usa nombres. 
                    // Sin embargo, para mayor precisión usamos el nombre del artista y álbum exactos.
                    val artistName = album.artistaId?.let { artistaRepository.getArtistaById(it)?.nombre }
                    val canciones = cancionRepository.searchCanciones(null, artistName, album.nombre)
                    call.respond(HttpStatusCode.OK, canciones)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al obtener canciones del álbum: ${e.message}"))
                }
            }

            patch("/albums/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden editar álbumes"))
                    return@patch
                }
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@patch
                    }

                    val existing = albumRepository.getAlbumById(id)
                    if (existing == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                        return@patch
                    }

                    val multipart = call.receiveMultipart()
                    var nombre: String? = null
                    var newUrlPortada: String? = null
                    var artistaId: Int? = null
                    val albumDir = File("archivos/albums").apply { mkdirs() }

                    multipart.forEachPart { part ->
                        when(part) {
                            is PartData.FormItem -> {
                                when(part.name) {
                                    "nombre" -> nombre = part.value
                                    "artistaId" -> artistaId = part.value.toIntOrNull()
                                }
                            }
                            is PartData.FileItem -> {
                                if (part.name == "portada") {
                                    newUrlPortada = saveFile(part, albumDir, "/archivos/albums")
                                }
                            }
                            else -> Unit
                        }
                        part.dispose()
                    }

                    val updated = albumRepository.updateAlbum(id, nombre, newUrlPortada, artistaId)
                    if (updated != null) {
                        if (!newUrlPortada.isNullOrBlank() && !existing.portadaUrl.isNullOrBlank()) {
                            deleteLocalFile(existing.portadaUrl)
                        }
                        call.respond(HttpStatusCode.OK, updated)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar álbum: ${e.message}"))
                }
            }

            delete("/albums/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val isAdmin = principal?.getClaim("admin", Int::class) == 1
                if (!isAdmin) {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Solo los administradores pueden borrar álbumes"))
                    return@delete
                }
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@delete
                    }
                    val ok = albumRepository.deleteAlbum(id)
                    if (ok) call.respond(HttpStatusCode.OK, mapOf("message" to "Álbum eliminado"))
                    else call.respond(HttpStatusCode.NotFound, mapOf("error" to "Álbum no encontrado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al eliminar álbum: ${e.message}"))
                }
            }

            // --- Playlists ---
            post("/listas") {
                try {
                    // Usamos JsonObject para máxima flexibilidad con tipos numéricos/strings
                    val body = call.receive<JsonObject>()
                    val nombre = body["nombre"]?.jsonPrimitive?.content ?: ""
                    val idUsuario = body["idUsuario"]?.jsonPrimitive?.longOrNull ?: 0L

                    if (nombre.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El nombre de la lista es obligatorio"))
                        return@post
                    }

                    val created = listaCancionesRepository.createLista(ListaCanciones(nombre = nombre, idUsuario = idUsuario))
                    if (created == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuario no encontrado"))
                    } else {
                        call.respond(HttpStatusCode.Created, created)
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al crear lista: ${e.message}")
                    )
                }
            }

            get("/usuarios/{id}/listas") {
                try {
                    val idUsuario = call.parameters["id"]?.toLongOrNull()
                    if (idUsuario == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de usuario inválido"))
                        return@get
                    }

                    val listas = listaCancionesRepository.getListasByUsuario(idUsuario)
                    call.respond(HttpStatusCode.OK, listas)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al obtener listas: ${e.message}")
                    )
                }
            }

            get("/listas/{id}/canciones") {
                try {
                    val idLista = call.parameters["id"]?.toLongOrNull()
                    if (idLista == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de lista inválido"))
                        return@get
                    }

                    val lista = listaCancionesRepository.getListaById(idLista)
                    if (lista == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lista no encontrada"))
                        return@get
                    }

                    val canciones = listaCancionesRepository.getCancionesByLista(idLista)
                    call.respond(HttpStatusCode.OK, canciones)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al obtener canciones de la lista: ${e.message}")
                    )
                }
            }

            post("/listas/{id}/canciones") {
                try {
                    val idLista = call.parameters["id"]?.toLongOrNull()
                    if (idLista == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de lista inválido"))
                        return@post
                    }

                    val body = call.receive<JsonObject>()
                    val idCancion = body["idCancion"]?.jsonPrimitive?.intOrNull ?: 0
                    val added = listaCancionesRepository.addCancionToLista(idLista, idCancion)

                    if (added) {
                        call.respond(HttpStatusCode.Created, mapOf("message" to "Canción agregada a la lista"))
                    } else {
                        call.respond(
                            HttpStatusCode.Conflict,
                            mapOf("error" to "La canción ya está en la lista o no existen lista/canción")
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Error al agregar canción a la lista: ${e.message}")
                    )
                }
            }

            patch("/listas/{id}") {
                try {
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@patch
                    }
                    val body = call.receive<JsonObject>()
                    val nombre = body["nombre"]?.jsonPrimitive?.content ?: ""
                    val updated = listaCancionesRepository.updateLista(id, ListaCanciones(nombre = nombre))
                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lista no encontrada"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            delete("/listas/{id}") {
                try {
                    val id = call.parameters["id"]?.toLongOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@delete
                    }
                    val deleted = listaCancionesRepository.deleteLista(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Lista eliminada"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lista no encontrada"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            delete("/listas/{idLista}/canciones/{idCancion}") {
                try {
                    val idLista = call.parameters["idLista"]?.toLongOrNull()
                    val idCancion = call.parameters["idCancion"]?.toIntOrNull()
                    if (idLista == null || idCancion == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                        return@delete
                    }
                    val removed = listaCancionesRepository.removeCancionFromLista(idLista, idCancion)
                    if (removed) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Canción quitada de la lista"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "No se encontró la canción en la lista"))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
        
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

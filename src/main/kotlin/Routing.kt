/**
 * Definición de rutas HTTP de la API.
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
import io.ktor.http.*
import io.ktor.http.content.*
import com.data.persistence.repository.PersistenceUsuarioRepository
import com.data.persistence.repository.PersistenceCancionRepository
import com.data.persistence.repository.PersistenceGeneroRepository
import com.data.persistence.repository.PersistenceListaCancionesRepository
import com.domain.usecase.ProviderUseCase
import com.domain.models.Usuario
import com.domain.models.UpdateUsuario
import com.domain.models.Cancion
import com.domain.models.Genero
import com.domain.models.ListaCanciones
import com.domain.models.ListaCancionesCancionRequest
import java.io.File
import java.util.UUID

fun Application.configureRouting() {
    val repository = PersistenceUsuarioRepository()
    val registerUseCase = ProviderUseCase.provideRegisterUseCase(repository)
    val loginUseCase = ProviderUseCase.provideLoginUseCase(repository)
    val cancionRepository = PersistenceCancionRepository()
    val generoRepository = PersistenceGeneroRepository()
    val listaCancionesRepository = PersistenceListaCancionesRepository()
    
    routing {
        get("/") {
            call.respondText("¡API Spotifake funcionando! ")
        }
        
        post("/register") {
            try {
                val updateUsuario = call.receive<UpdateUsuario>()
                val usuario = registerUseCase(updateUsuario)
                
                if (usuario != null) {
                    usuario.pass = ""
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
        
        post("/login") {
            try {
                val credentials = call.receive<UpdateUsuario>()
                val usuario = loginUseCase(credentials.correo, credentials.pass)
                
                if (usuario != null) {
                    usuario.pass = ""
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
        
        get("/usuarios") {
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
        
        put("/usuarios/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
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
        
        delete("/usuarios/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
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

        post("/canciones") {
            val multipart = call.receiveMultipart()
            var nombre: String? = null
            var artista: String? = null
            var album: String? = null
            var genero: Int? = null
            var likes: Int = 0
            var urlAudio: String? = null
            var urlPortada: String? = null

            val audioDir = File("archivos/audio").apply { mkdirs() }
            val portadaDir = File("archivos/portadas").apply { mkdirs() }

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "nombre" -> nombre = part.value
                            "artista" -> artista = part.value
                            "album" -> album = part.value
                            "genero" -> genero = part.value.toIntOrNull()
                            "likes" -> likes = part.value.toIntOrNull() ?: 0
                        }
                    }
                    is PartData.FileItem -> {
                        when (part.name) {
                            "audio" -> urlAudio = saveFile(part, audioDir, "/archivos/audio")
                            "portada" -> urlPortada = saveFile(part, portadaDir, "/archivos/portadas")
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (nombre.isNullOrBlank() || artista.isNullOrBlank() || album.isNullOrBlank() || genero == null || urlAudio.isNullOrBlank() || urlPortada.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Faltan campos obligatorios o archivos"))
                return@post
            }

            val cancion = cancionRepository.createCancion(
                Cancion(
                    nombre = nombre!!,
                    artista = artista!!,
                    album = album!!,
                    genero = genero!!,
                    likes = likes,
                    urlAudio = urlAudio!!,
                    urlPortada = urlPortada!!
                )
            )

            call.respond(HttpStatusCode.Created, cancion)
        }

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

            val updated = cancionRepository.updateCancion(
                id,
                nombre,
                artista,
                album,
                genero,
                likes,
                newUrlAudio,
                newUrlPortada
            )

            if (updated == null) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Error al actualizar la canción"))
                return@put
            }

            if (!newUrlAudio.isNullOrBlank()) {
                deleteLocalFile(existing.urlAudio)
            }
            if (!newUrlPortada.isNullOrBlank()) {
                deleteLocalFile(existing.urlPortada)
            }

            call.respond(HttpStatusCode.OK, updated)
        }

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

            deleteLocalFile(deleted.urlAudio)
            deleteLocalFile(deleted.urlPortada)

            call.respond(HttpStatusCode.OK, mapOf("message" to "Canción eliminada correctamente"))
        }

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

        delete("/generos/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@delete
                }

                val deleted = generoRepository.deleteGenero(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Género eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Género no encontrado"))
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar género: ${e.message}")
                )
            }
        }

        post("/listas") {
            try {
                val lista = call.receive<ListaCanciones>()
                if (lista.nombre.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "El nombre de la lista es obligatorio"))
                    return@post
                }

                val created = listaCancionesRepository.createLista(lista)
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

                val body = call.receive<ListaCancionesCancionRequest>()
                val added = listaCancionesRepository.addCancionToLista(idLista, body.idCancion)

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

        delete("/listas/{idLista}/canciones/{idCancion}") {
            try {
                val idLista = call.parameters["idLista"]?.toLongOrNull()
                val idCancion = call.parameters["idCancion"]?.toIntOrNull()

                if (idLista == null || idCancion == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "IDs inválidos"))
                    return@delete
                }

                val removed = listaCancionesRepository.removeCancionFromLista(idLista, idCancion)
                if (removed) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Canción eliminada de la lista"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Relación lista-canción no encontrada"))
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar canción de la lista: ${e.message}")
                )
            }
        }

        delete("/listas/{id}") {
            try {
                val idLista = call.parameters["id"]?.toLongOrNull()
                if (idLista == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID de lista inválido"))
                    return@delete
                }

                val deleted = listaCancionesRepository.deleteLista(idLista)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Lista eliminada correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Lista no encontrada"))
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Error al eliminar lista: ${e.message}")
                )
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

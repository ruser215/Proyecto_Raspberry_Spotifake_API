/**
 * Pruebas base de la aplicación Ktor.
 * Sirven como punto de partida para validar endpoints y configuración.
 */
package com.example

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import com.domain.models.Artista
import com.domain.models.Album

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testArtistasCrud() = testApplication {
        application { module() }
        // crear un artista
        val artista = Artista(nombre = "TestArtist", fotoUrl = null)
        val created = client.post("/artistas") {
            contentType(ContentType.Application.Json)
            setBody(artista)
        }.body<Artista>()
        assertEquals("TestArtist", created.nombre)
        // obtener listado
        val list = client.get("/artistas").body<List<Artista>>()
        assert(list.any { it.nombre == "TestArtist" })
        // actualizar
        val updated = client.put("/artistas/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody(created.copy(nombre = "Changed"))
        }.body<Artista>()
        assertEquals("Changed", updated.nombre)
        // eliminar
        val delResp = client.delete("/artistas/${created.id}")
        assertEquals(HttpStatusCode.OK, delResp.status)
    }

    @Test
    fun testAlbumCrud() = testApplication {
        application { module() }
        // primero crear un artista para asociar
        val artista = Artista(nombre = "AlbumArtist")
        val createdArtist = client.post("/artistas") {
            contentType(ContentType.Application.Json)
            setBody(artista)
        }.body<Artista>()

        val album = Album(nombre = "TestAlbum", artistaId = createdArtist.id)
        val created = client.post("/artistas/${createdArtist.id}/albums") {
            contentType(ContentType.Application.Json)
            setBody(album)
        }.body<Album>()
        assertEquals("TestAlbum", created.nombre)

        // borrar
        val resp = client.delete("/albums/${created.id}")
        assertEquals(HttpStatusCode.OK, resp.status)
    }

}

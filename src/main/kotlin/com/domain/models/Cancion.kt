/**
 * Modelo de dominio de canción.
 * Define los datos mínimos para listar, reproducir y clasificar canciones.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Cancion(
    val id: Int? = null,
    var nombre: String,
    var artistaId: Int? = null, // ID del artista
    var albumId: Int? = null,   // ID del álbum
    var artista: String? = null, // Nombre del artista (para el frontend)
    var album: String? = null,   // Nombre del álbum (para el frontend)
    var genero: Int,
    var likes: Int = 0,
    var urlAudio: String,
    var urlPortada: String? = null
)

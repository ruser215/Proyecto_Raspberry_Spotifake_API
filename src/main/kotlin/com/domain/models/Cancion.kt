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
    var artistaId: Int? = null,
    var artistasIds: List<Int>? = emptyList(),
    var albumId: Int? = null,
    var albumIds: List<Int>? = emptyList(),
    var artista: String? = null,
    var artistas: List<String>? = emptyList(),
    var album: String? = null,
    var albumes: List<String>? = emptyList(),
    var genero: Int,
    var likes: Int = 0,
    var reproducciones: Int = 0,
    var urlAudio: String,
    var urlPortada: String? = null
)

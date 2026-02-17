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
    var artista: String,
    var album: String,
    var genero: Int,
    var likes: Int = 0,
    var urlAudio: String,
    var urlPortada: String
)

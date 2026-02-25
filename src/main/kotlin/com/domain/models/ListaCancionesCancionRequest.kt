/**
 * DTO para agregar una canción a una lista.
 * Encapsula el identificador de canción enviado en el body.
 */
package com.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ListaCancionesCancionRequest(
    val idCancion: Int
)

/**
 * Contrato de repositorio de géneros.
 * Permite mantener un catálogo de categorías musicales.
 */
package com.domain.repository

import com.domain.models.Genero

interface GeneroInterface {
    suspend fun createGenero(genero: Genero): Genero
    suspend fun getAllGeneros(): List<Genero>
    suspend fun getGeneroById(id: Int): Genero?
    suspend fun deleteGenero(id: Int): Boolean
}

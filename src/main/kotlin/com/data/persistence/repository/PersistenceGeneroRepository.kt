/**
 * Implementación de repositorio de géneros.
 * Encapsula operaciones de mantenimiento del catálogo de géneros.
 */
package com.data.persistence.repository

import com.domain.models.Genero
import com.domain.repository.GeneroInterface
import GeneroDao
import GeneroTable
import suspendTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

class PersistenceGeneroRepository : GeneroInterface {
    override suspend fun createGenero(genero: Genero): Genero = suspendTransaction {
        GeneroDao.new {
            nombre = genero.nombre
        }.toGenero()
    }

    override suspend fun getAllGeneros(): List<Genero> = suspendTransaction {
        GeneroDao.all().map { it.toGenero() }
    }

    override suspend fun deleteGenero(id: Int): Boolean = suspendTransaction {
        val deleted = GeneroTable.deleteWhere { GeneroTable.id eq id }
        deleted == 1
    }
}

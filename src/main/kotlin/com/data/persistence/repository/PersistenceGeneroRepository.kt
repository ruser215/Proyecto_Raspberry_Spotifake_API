/**
 * Implementación de repositorio de géneros.
 * Encapsula operaciones de mantenimiento del catálogo de géneros.
 */
package com.data.persistence.repository

import com.domain.models.Genero
import com.domain.repository.GeneroInterface
import com.data.persistence.models.*
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

    override suspend fun getGeneroById(id: Int): Genero? = suspendTransaction {
        GeneroDao.findById(id)?.toGenero()
    }

    override suspend fun updateGenero(id: Int, nombre: String): Genero? = suspendTransaction {
        val existing = GeneroDao.findById(id)
        existing?.apply {
            this.nombre = nombre
        }?.toGenero()
    }

    override suspend fun deleteGenero(id: Int): Boolean = suspendTransaction {
        val deleted = GeneroTable.deleteWhere { GeneroTable.id eq id }
        deleted == 1
    }
}

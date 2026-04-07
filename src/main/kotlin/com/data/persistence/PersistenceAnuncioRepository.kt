package com.data.persistence

import com.data.persistence.models.AnuncioTable
import com.domain.models.Anuncio
import com.domain.repository.AnuncioRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class PersistenceAnuncioRepository : AnuncioRepository {
    override suspend fun getRandomAd(): Anuncio? = transaction {
        val query = AnuncioTable.selectAll().where { AnuncioTable.activo eq true }
        // Simple random logic for Exposed, fallback to kotlin random
        val randomRow = query.toList().shuffled().firstOrNull()
        randomRow?.toAnuncio()
    }

    private fun ResultRow.toAnuncio(): Anuncio = Anuncio(
        id = this[AnuncioTable.id].value,
        titulo = this[AnuncioTable.titulo],
        descripcion = this[AnuncioTable.descripcion],
        urlBanner = this[AnuncioTable.urlBanner],
        urlPoster = this[AnuncioTable.urlPoster],
        urlAudio = this[AnuncioTable.urlAudio],
        fecha = this[AnuncioTable.fecha],
        idEmpresa = this[AnuncioTable.idEmpresa].value,
        idCancion = this[AnuncioTable.idCancion].value,
        activo = this[AnuncioTable.activo]
    )
}

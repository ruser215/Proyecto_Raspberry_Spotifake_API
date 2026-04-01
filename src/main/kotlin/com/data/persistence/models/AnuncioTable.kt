
package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object AnuncioTable : IntIdTable("anuncio") {
    val titulo = varchar("titulo", 300)
    val descripcion = varchar("descripcion", 1000)
    val urlBanner = varchar("url_banner", 300)
    val urlPoster = varchar("url_poster", 300)
    val urlAudio = varchar("url_audio", 300)
    val fecha = date("fecha")
    val idEmpresa = reference("id_empresa", EmpresaTable)
    val idCancion = reference("id_cancion", CancionTable)
    val activo = bool("activo").default(true)
}

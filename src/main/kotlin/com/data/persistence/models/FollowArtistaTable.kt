package com.data.persistence.models

package com.data.persistence.models
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object FollowArtistaTable : Table("follow_artista") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val idArtista = reference("id_artista", ArtistaTable)
    val fecha = date("fecha")
    override val primaryKey = PrimaryKey(idUsuario, idArtista)
}

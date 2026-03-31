package com.data.persistence.models

import org.jetbrains.exposed.sql.Table

object ColaboradorPlaylistTable : Table("colaborador_playlist") {
    val idPlaylist = reference("id_playlist", PlaylistTable)
    val idColaborador = reference("id_colaborador", UsuarioTable)
    val idRol = reference("id_rol", RolTable)
    override val primaryKey = PrimaryKey(idPlaylist, idColaborador)
}

package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object SolicitudColabPlaylistTable : IntIdTable("solicitud_colab_playlist") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val idPlaylist = reference("id_playlist", PlaylistTable)
    val idColaborador = reference("id_colaborador", UsuarioTable)
    val idRol = reference("id_rol", RolTable)
    val fecha = date("fecha")
}

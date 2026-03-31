package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object ReproduccionTable : IntIdTable("reproduccion") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val idCancion = reference("id_cancion", CancionTable)
    val fecha = date("fecha")
    val segundosEscuchados = integer("segundos_escuchados")
}

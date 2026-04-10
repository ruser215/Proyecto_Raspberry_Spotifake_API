package com.data.persistence.models

import org.jetbrains.exposed.dao.id.IntIdTable

object AlarmaTable : IntIdTable("alarma") {
    val idUsuario = reference("id_usuario", UsuarioTable)
    val nombreAlarma = varchar("nombre_alarma", 300)
    val idEmpresa = reference("id_empresa", EmpresaTable).nullable()
    val hora = varchar("hora", 10)
    val idCancion = reference("id_cancion", CancionTable)
    val activo = bool("activo").default(true)
}

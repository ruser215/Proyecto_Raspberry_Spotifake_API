/**
 * Tabla de listas de canciones por usuario.
 * Relaciona una lista con su propietario en el sistema.
 */
import org.jetbrains.exposed.dao.id.LongIdTable

object ListaCancionesTable : LongIdTable("lista_canciones") {
    val nombre = varchar("nombre", 100)
    val idUsuario = reference("id_usuario", UsuarioTable)
}

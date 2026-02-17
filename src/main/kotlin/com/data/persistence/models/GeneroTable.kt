/**
 * Tabla de géneros musicales.
 * Se usa como catálogo de referencia para clasificar canciones.
 */
import org.jetbrains.exposed.dao.id.IntIdTable

object GeneroTable : IntIdTable("generos") {
    val nombre = varchar("nombre", 100)
}

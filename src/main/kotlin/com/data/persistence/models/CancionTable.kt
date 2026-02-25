/**
 * Tabla de canciones.
 * Incluye metadatos de la canción y enlaces a los recursos de audio/portada.
 */
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import com.data.persistence.models.AlbumTable

object CancionTable : IntIdTable("canciones") {
    val nombre = varchar("nombre", 200)
    // referencia al álbum, de ahí se obtendrá el artista
    val album = reference("album", AlbumTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val genero = reference("genero", GeneroTable)
    val likes = integer("likes").default(0)
    val urlAudio = varchar("urlaudio", 255)
    val urlPortada = varchar("urlportada", 255)
}

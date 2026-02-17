/**
 * Tabla de canciones.
 * Incluye metadatos de la canción y enlaces a los recursos de audio/portada.
 */
import org.jetbrains.exposed.dao.id.IntIdTable

object CancionTable : IntIdTable("canciones") {
    val nombre = varchar("nombre", 200)
    val artista = varchar("artista", 100)
    val album = varchar("album", 200)
    val genero = reference("genero", GeneroTable)
    val likes = integer("likes").default(0)
    val urlAudio = varchar("urlaudio", 255)
    val urlPortada = varchar("urlportada", 255)
}

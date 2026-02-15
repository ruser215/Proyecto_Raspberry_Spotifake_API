import org.jetbrains.exposed.dao.id.IntIdTable

object CancionTable : IntIdTable("canciones") {
    val nombre = varchar("nombre", 150)
    val artista = varchar("artista", 150)
    val album = varchar("album", 150)
    val audioUrl = varchar("audio_url", 255)
    val portadaUrl = varchar("portada_url", 255)
}

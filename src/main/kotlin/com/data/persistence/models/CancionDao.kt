/**
 * DAO de canción.
 * Encapsula la conversión entre entidad de base de datos y modelo de dominio.
 */
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Cancion
import com.data.persistence.models.AlbumDao

class CancionDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CancionDao>(CancionTable)

    var nombre by CancionTable.nombre
    // cada canción apunta a un álbum (puede ser null si no se asignó)
    var album by AlbumDao optionalReferencedOn CancionTable.album
    var genero by CancionTable.genero
    var likes by CancionTable.likes
    var urlAudio by CancionTable.urlAudio
    var urlPortada by CancionTable.urlPortada

    fun toCancion(): Cancion {
        // armamos nombres legibles a partir del álbum/artista
        val albumNombre = album?.nombre ?: ""
        val artistaNombre = album?.artista?.nombre ?: ""
        return Cancion(
            id = this.id.value,
            nombre = this.nombre,
            artista = artistaNombre,
            album = albumNombre,
            genero = this.genero.value,
            likes = this.likes,
            urlAudio = this.urlAudio,
            urlPortada = this.urlPortada
        )
    }
}

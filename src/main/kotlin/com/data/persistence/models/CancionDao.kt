import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Cancion

class CancionDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CancionDao>(CancionTable)

    var nombre by CancionTable.nombre
    var artista by CancionTable.artista
    var album by CancionTable.album
    var audioUrl by CancionTable.audioUrl
    var portadaUrl by CancionTable.portadaUrl

    fun toCancion(): Cancion {
        return Cancion(
            id = this.id.value,
            nombre = this.nombre,
            artista = this.artista,
            album = this.album,
            audioUrl = this.audioUrl,
            portadaUrl = this.portadaUrl
        )
    }
}

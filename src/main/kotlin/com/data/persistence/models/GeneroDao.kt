/**
 * DAO de género musical.
 * Permite transformar registros de géneros en objetos de dominio.
 */
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Genero

class GeneroDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GeneroDao>(GeneroTable)

    var nombre by GeneroTable.nombre

    fun toGenero(): Genero {
        return Genero(
            id = this.id.value,
            nombre = this.nombre
        )
    }
}

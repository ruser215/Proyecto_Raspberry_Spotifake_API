/**
 * Tabla pivote entre listas y canciones.
 * La clave compuesta impide insertar la misma canción dos veces en la misma lista.
 */
import org.jetbrains.exposed.sql.Table

object ListaCancionesCancionesTable : Table("lista_canciones_canciones") {
    val idCancion = reference("id_cancion", CancionTable)
    val idLista = reference("id_lista", ListaCancionesTable)

    override val primaryKey = PrimaryKey(idCancion, idLista)

    init {
        uniqueIndex(idCancion, idLista)
    }
}

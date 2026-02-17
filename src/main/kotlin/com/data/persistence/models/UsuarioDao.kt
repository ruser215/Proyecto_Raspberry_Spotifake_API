/**
 * DAO de usuario con Exposed.
 * Mapea cada fila de la tabla de usuarios a objetos de dominio Kotlin.
 */
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Usuario

class UsuarioDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UsuarioDao>(UsuarioTable)

    var nombre by UsuarioTable.nombre
    var apellido1 by UsuarioTable.apellido1
    var apellido2 by UsuarioTable.apellido2
    var correo by UsuarioTable.correo
    var admin by UsuarioTable.admin
    var premium by UsuarioTable.premium
    var pass by UsuarioTable.pass
    var token by UsuarioTable.token
    var urlImagen by UsuarioTable.urlImagen
    
    
    fun toUsuario(): Usuario {
        return Usuario(
            id = this.id.value,
            nombre = this.nombre,
            apellido1 = this.apellido1,
            apellido2 = this.apellido2,
            correo = this.correo,
            admin = this.admin,
            premium = this.premium,
            pass = this.pass,
            token = this.token,
            urlImagen = this.urlImagen
        )
    }
}
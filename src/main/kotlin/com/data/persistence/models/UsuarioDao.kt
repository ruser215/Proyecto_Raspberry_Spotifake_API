import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.domain.models.Usuario

class UsuarioDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UsuarioDao>(UsuarioTable)

    var nombre by UsuarioTable.nombre
    var apellido1 by UsuarioTable.apellido1
    var apellido2 by UsuarioTable.apellido2
    var nickname by UsuarioTable.nickname
    var contrasena by UsuarioTable.contrasena // Usamos 'contrasena' para evitar caracteres especiales si prefieres
    
    /**
     * Convierte el DAO a un objeto de dominio Usuario.
     */
    fun toUsuario(): Usuario {
        return Usuario(
            id = this.id.value,
            nombre = this.nombre,
            apellido1 = this.apellido1,
            apellido2 = this.apellido2,
            nickname = this.nickname,
            contrasena = this.contrasena
        )
    }
}
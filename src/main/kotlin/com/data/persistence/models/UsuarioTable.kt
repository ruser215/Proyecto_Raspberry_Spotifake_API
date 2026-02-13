import org.jetbrains.exposed.dao.id.IntIdTable

object UsuarioTable : IntIdTable("usuarios") { // Asumiendo que la tabla se llama "usuarios"
    val nombre = varchar("nombre", 25)
    val apellido1 = varchar("apellido1", 50)
    val apellido2 = varchar("apellido2", 50)
    val nickname = varchar("nickname", 25)
    val contrasena = varchar("contraseña", 60) // BCrypt hashes necesitan 60 caracteres
}
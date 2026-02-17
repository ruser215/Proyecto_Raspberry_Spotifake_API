/**
 * Tabla de usuarios en base de datos.
 * Define columnas, tipos y valores por defecto del módulo de cuentas.
 */
import org.jetbrains.exposed.dao.id.LongIdTable

object UsuarioTable : LongIdTable("usuarios") {
    val nombre = varchar("nombre", 25)
    val apellido1 = varchar("apellido1", 50)
    val apellido2 = varchar("apellido2", 50)
    val correo = varchar("correo", 50)
    val admin = bool("admin").default(false)
    val premium = bool("premium").default(false)
    val pass = varchar("pass", 60)
    val token = varchar("token", 60).default("")
    val urlImagen = varchar("urlimagen", 255).nullable()
}
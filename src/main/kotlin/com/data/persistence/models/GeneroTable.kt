
package com.data.persistence.models
import org.jetbrains.exposed.dao.id.IntIdTable

object GeneroTable : IntIdTable("genero") {
    val nombre = varchar("nombre", 150)
}

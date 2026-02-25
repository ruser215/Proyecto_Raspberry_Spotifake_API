/**
 * Configuración de acceso a base de datos con Hikari + Exposed.
 * Gestiona conexión, creación de esquema y opción de reinicio para desarrollo.
 */
package com.data.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import UsuarioTable
import CancionTable
import GeneroTable
import ListaCancionesTable
import ListaCancionesCancionesTable
import com.data.persistence.models.ArtistTable
import com.data.persistence.models.AlbumTable


object DatabaseFactory {
    
    private val dotenv = dotenv {
        ignoreIfMissing = true
        directory = "."
    }
    
    
    fun init() {
        val database = Database.connect(createHikariDataSource())
        val resetOnStart = dotenv["DB_RESET_ON_START"]?.toBoolean() ?: false
        
        transaction(database) {
            if (resetOnStart) {
                // en desarrollo es más sencillo reconstruir el esquema completo
                SchemaUtils.drop(
                    ListaCancionesCancionesTable,
                    ListaCancionesTable,
                    CancionTable,
                    AlbumTable,
                    ArtistTable,
                    GeneroTable,
                    UsuarioTable
                )
            }
            // si el esquema se cambia y ya existen datos, configure DB_RESET_ON_START=true
            // o implemente un script de migración que:
            // 1. cree ArtistTable y AlbumTable
            // 2. recorra los valores distintos de "artista" y "album" de la tabla canciones
            // 3. inserte registros en artistas/álbums y actualice el campo album (FK) de canciones
            // 4. opcionalmente elimine las columnas de texto antiguas
            
            SchemaUtils.create(
                UsuarioTable,
                GeneroTable,
                ArtistTable,
                AlbumTable,
                CancionTable,
                ListaCancionesTable,
                ListaCancionesCancionesTable
            )
        }
    }
    
    
    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = dotenv["MYSQL_DRIVER"] ?: "org.mariadb.jdbc.Driver"
            jdbcUrl = dotenv["MYSQL_JDBC_URL"]
                ?: "jdbc:mariadb://${dotenv["MYSQL_HOST"] ?: "localhost"}:${dotenv["MYSQL_HOST_PORT"] ?: "3306"}/${dotenv["MYSQL_DATABASE"] ?: "spotyfake"}"
            username = dotenv["MYSQL_USER"] ?: "admin"
            password = dotenv["MYSQL_PASSWORD"] ?: "s8n38DGB8d72"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}

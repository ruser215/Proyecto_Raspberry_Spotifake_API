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
                SchemaUtils.drop(ListaCancionesCancionesTable, ListaCancionesTable, CancionTable, GeneroTable, UsuarioTable)
            }
            SchemaUtils.create(UsuarioTable, GeneroTable, CancionTable, ListaCancionesTable, ListaCancionesCancionesTable)
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

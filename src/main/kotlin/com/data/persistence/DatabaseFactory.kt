package com.data.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import UsuarioTable
import CancionTable

/**
 * Configuración y inicialización de la base de datos.
 */
object DatabaseFactory {
    
    private val dotenv = dotenv {
        ignoreIfMissing = true
        directory = "."
    }
    
    /**
     * Inicializa la conexión a la base de datos y crea las tablas.
     */
    fun init() {
        val database = Database.connect(createHikariDataSource())
        
        // Crear tablas si no existen
        transaction(database) {
            SchemaUtils.create(UsuarioTable, CancionTable)
        }
    }
    
    /**
     * Crea y configura el pool de conexiones HikariCP.
     */
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

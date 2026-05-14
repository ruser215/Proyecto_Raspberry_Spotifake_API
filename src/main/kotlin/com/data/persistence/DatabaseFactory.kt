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
import com.data.persistence.models.*
// import com.data.persistence.models.ArtistTable (no existe, usar ArtistaTable)
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
                nukeDatabase()
            }
            
            SchemaUtils.create(
                UsuarioTable,
                GeneroTable,
                ArtistaTable,
                AlbumTable,
                CancionTable,
                LetraTable,
                LetraSyncTable,
                ReproduccionTable,
                ListaCancionesTable,
                ListaCancionesCancionesTable,
                SongArtistsTable,
                SongAlbumsTable,
                AlbumArtistsTable,
                AlbumCancionTable,
                FollowArtistaTable,
                SolicitudColabPlaylistTable,
                AnuncioTable,
                MascotaTable,
                AmistadTable,
                ColaboradorPlaylistTable,
                ArtistaCancionTable,
                EmpresaTable,
                UsuarioMascotaTable,
                PlaylistCancionTable,
                GeneroCancionTable,
                AlarmaTable,
                EstadoNotificacionTable,
                EstadoSolicitudTable,
                ArtistaAlbumTable,
                LikeCancionTable,
                TipoMascotaTable,
                TemaTable,
                RolTable,
                PlaylistTable
            )

            // Asegurar directorios de archivos
            java.io.File("archivos/usuarios").mkdirs()
            java.io.File("uploads/usuarios").mkdirs()
        }
    }

    fun nukeDatabase() {
        transaction {
            exec("SET FOREIGN_KEY_CHECKS = 0;")
            SchemaUtils.drop(
                UsuarioTable, GeneroTable, ArtistaTable, AlbumTable, CancionTable, LetraTable,
                LetraSyncTable, ReproduccionTable, ListaCancionesTable, ListaCancionesCancionesTable,
                SongArtistsTable, SongAlbumsTable, AlbumArtistsTable, AlbumCancionTable,
                FollowArtistaTable, SolicitudColabPlaylistTable, AnuncioTable, MascotaTable,
                AmistadTable, ColaboradorPlaylistTable, ArtistaCancionTable, EmpresaTable,
                UsuarioMascotaTable, PlaylistCancionTable, GeneroCancionTable, AlarmaTable,
                EstadoNotificacionTable, EstadoSolicitudTable, ArtistaAlbumTable, LikeCancionTable,
                TipoMascotaTable, TemaTable, RolTable, PlaylistTable
            )
            SchemaUtils.create(
                UsuarioTable, GeneroTable, ArtistaTable, AlbumTable, CancionTable, LetraTable,
                LetraSyncTable, ReproduccionTable, ListaCancionesTable, ListaCancionesCancionesTable,
                SongArtistsTable, SongAlbumsTable, AlbumArtistsTable, AlbumCancionTable,
                FollowArtistaTable, SolicitudColabPlaylistTable, AnuncioTable, MascotaTable,
                AmistadTable, ColaboradorPlaylistTable, ArtistaCancionTable, EmpresaTable,
                UsuarioMascotaTable, PlaylistCancionTable, GeneroCancionTable, AlarmaTable,
                EstadoNotificacionTable, EstadoSolicitudTable, ArtistaAlbumTable, LikeCancionTable,
                TipoMascotaTable, TemaTable, RolTable, PlaylistTable
            )
            exec("SET FOREIGN_KEY_CHECKS = 1;")
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

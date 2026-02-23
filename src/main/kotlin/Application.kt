/**
 * Punto de entrada principal de la API con Ktor.
 * En DAM, este archivo se usa para entender cómo se inicializa la aplicación,
 * se carga la base de datos y se registran los módulos de infraestructura.
 */
package com.example

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*
import com.data.persistence.DatabaseFactory

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    
    configureSerialization()
    configureSecurity()
    configureRouting()
}

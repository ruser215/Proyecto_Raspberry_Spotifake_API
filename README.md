# ktor-sample

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
| ------------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [Static Content](https://start.ktor.io/p/static-content)               | Serves static files from defined locations                                         |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
| -----------------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

# Proyecto_Raspberry_Spotifake_API

## Nuevas tablas normalizadas
Se introducen las entidades `artistas` y `albums` para evitar repetir el nombre de artista/álbum en cada canción.

- **artistas**: id, nombre único, fotourl opcional
- **albums**: id, nombre, fk a artista, portadaurl opcional
- **canciones**: ahora referencia a un `album` en lugar de guardar texto; las columnas antiguas se documentaron en comentarios y se pueden migrar.

### Migración de datos existentes
Si ya tienes un esquema anterior con campo `artista` y `album` en la tabla `canciones`, puedes proceder de dos formas:

1. **Reiniciar la base de datos** estableciendo `DB_RESET_ON_START=true` en el `.env` y dejando que el servidor recree el esquema vacío.
2. **Migrar manualmente**:
   - Crear los registros en `artistas` y `albums` tomando los valores distintos de la tabla `canciones`.
   - Actualizar cada fila de `canciones` para apuntar con `album = <nuevo_id>`.
   - Eliminar las columnas de texto antiguas si ya no son necesarias.

## Endpoints nuevos y modificados
Se agregan rutas CRUD completas para gestionar artistas y álbumes. También se conservan rutas para listar canciones por artista/álbum.

### Artistas
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST   | `/artistas` | Crea artista (nombre obligatorio) |
| GET    | `/artistas` | Lista todos o filtra por `?nombre=` |
| GET    | `/artistas/{id}` | Obtiene artista por id |
| PUT    | `/artistas/{id}` | Actualiza nombre/foto |
| DELETE | `/artistas/{id}` | Elimina artista |

### Álbumes
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST   | `/artistas/{id}/albums` | Crea álbum para artista |
| GET    | `/artistas/{id}/albums` | Lista álbumes de artista |
| GET    | `/albums/{id}` | Obtiene datos de álbum |
| GET    | `/albums/{id}/canciones` | Canciones del álbum |
| PUT    | `/albums/{id}` | Actualiza datos de álbum |
| DELETE | `/albums/{id}` | Elimina álbum |

### Canciones
La ruta de canciones se mantiene, pero ahora se espera que los campos `artista` y `album` se correspondan con nombres existentes; el repositorio crea categorías si no están.

### Interfaz Web de demostración
La página `src/main/resources/static/index.html` fue mejorada para mostrar los artistas como cards horizontales, navegar a álbumes (dos por fila) y ver la lista de canciones dentro de un álbum.


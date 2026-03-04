# Spotifake API

API REST desarrollada con **Ktor** (framework de Kotlin) para gestionar usuarios, canciones, artistas, albums, géneros y playlists. Pensada para ejecutarse en una **Raspberry Pi** mediante Docker, aunque también puede correr en cualquier máquina con Java 17.

---

## Tecnologias utilizadas

| Tecnologia | Para que sirve |
|---|---|
| Ktor (Netty) | Framework para crear la API en Kotlin |
| Exposed ORM | Libreria para trabajar con la base de datos desde Kotlin |
| MariaDB | Base de datos relacional (compatible con MySQL) |
| HikariCP | Pool de conexiones a la base de datos |
| JWT | Autenticacion mediante tokens (JSON Web Token) |
| BCrypt | Cifrado de contraseñas |
| Docker | Contenerizacion de la aplicacion |
| Nginx | Reverse proxy para exponer la API al exterior |

---

## Estructura del proyecto

```
src/
  main/
    kotlin/
      data/       -> Acceso a la base de datos (repositorios)
      domain/     -> Logica de negocio (casos de uso)
      routes/     -> Definicion de los endpoints
      plugins/    -> Configuracion de Ktor (auth, CORS, serialization...)
    resources/    -> application.yaml con la configuracion del servidor
```

La arquitectura sigue un patron por capas: las rutas reciben la peticion, llaman a un caso de uso (dominio) y este accede al repositorio (datos).

---

## Requisitos previos

- **Java 17** o superior
- **Docker** y **Docker Compose**
- (Opcional) **Nginx** si se quiere usar como reverse proxy

---

## Configuracion

Antes de arrancar, hay que crear un archivo `.env` en la raiz del proyecto. Ya existe uno de ejemplo con todos los valores necesarios:

```env
# Base de datos
MYSQL_ROOT_PASSWORD=tu_password
MYSQL_PASSWORD=tu_password
MYSQL_USER=admin
MYSQL_HOST=localhost
MYSQL_DATABASE=spotyfake

# Puertos
MYSQL_HOST_PORT=3307
KTOR_HOST_PORT=8001
ADMINER_HOST_PORT=8081

# JWT
JWT_SECRET=tu_clave_secreta
JWT_DOMAIN=https://tu-dominio/
JWT_AUDIENCE=jwt-audience
JWT_REALM=ktor-spotifake

# Docker (para publicar la imagen)
IMAGE_NAME=usuario/api-spotifake
IMAGE_TAG=latest
PLATFORM=linux/arm/v7
```

> **Importante:** No subas el `.env` a un repositorio publico. Contiene contraseñas y claves secretas.

---

## Como arrancar con Docker

```bash
# Construir e iniciar todos los contenedores
docker compose up -d --build

# Ver que contenedores estan corriendo
docker ps

# Ver los logs de la API
docker logs -f ktor-app

# Parar todos los contenedores
docker compose down
```

Los contenedores que se levantan son:

| Contenedor | Descripcion | Puerto |
|---|---|---|
| `ktor-app` | La API | `8001` |
| `ktor-db` | Base de datos MariaDB | `3307` |
| `ktor-adminer` | Interfaz web para la BD | `8081` |
| `pagina-web` | Frontend React | `3000` |

La API estara disponible en `http://localhost:8001/api`.

---

## Autenticacion

La mayoria de endpoints estan protegidos. Para acceder a ellos hay que incluir el token JWT en la cabecera de la peticion:

```
Authorization: Bearer <TOKEN>
```

El token se obtiene al registrarse o al hacer login.

---

## Endpoints

### Usuarios y autenticacion

#### Registro

```
POST /api/register
```

Body (JSON):
```json
{
  "username": "tomas",
  "correo": "tomas@red.com",
  "pass": "1234"
}
```

Solo se aceptan esos tres campos. Si se envia cualquier otro campo (como `admin` o `premium`) la respuesta sera un error `400 Bad Request`.

Respuesta `201 Created`:
```json
{
  "id": 1,
  "username": "tomas",
  "correo": "tomas@red.com",
  "token": "<TOKEN>"
}
```

#### Login

```
POST /api/login
```

Body (JSON):
```json
{
  "correo": "tomas@red.com",
  "pass": "1234"
}
```

Respuesta `200 OK`:
```json
{
  "id": 1,
  "username": "tomas",
  "correo": "tomas@red.com",
  "admin": 0,
  "premium": 0,
  "urlImagen": null,
  "token": "<TOKEN>"
}
```

---

### Usuarios (requiere token)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/usuarios` | Lista todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtiene un usuario por su ID |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario |

---

### Canciones (requiere token)

#### Crear cancion

```
POST /api/canciones
```

Formato: `multipart/form-data`

| Campo | Tipo | Descripcion |
|---|---|---|
| `nombre` | texto | Nombre de la cancion |
| `artistaId` | numero | ID del artista |
| `albumId` | numero | ID del album |
| `genero` | numero | ID del genero |
| `audio` | archivo | Archivo de audio (.mp3) |
| `portada` | archivo | Imagen de portada |

Respuesta:
```json
{
  "id": 5,
  "nombre": "Highway to Hell",
  "urlAudio": "/archivos/audio/xxx.mp3",
  "urlPortada": "/archivos/portadas/xxx.png"
}
```

#### Buscar canciones

```
GET /api/canciones?nombre=Highway&artista=AC
```

Los parametros de busqueda son opcionales. Devuelve un array con las canciones que coincidan.

#### Eliminar cancion

```
DELETE /api/canciones/{id}
```

---

### Artistas (requiere token)

#### Crear artista

```
POST /api/artistas
```

Formato: `multipart/form-data`

| Campo | Tipo | Descripcion |
|---|---|---|
| `nombre` | texto | Nombre del artista |
| `foto` | archivo | Imagen del artista |

Respuesta:
```json
{
  "id": 2,
  "nombre": "AC/DC",
  "fotoUrl": "/archivos/artistas/xxx.png"
}
```

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/artistas` | Lista todos los artistas |
| DELETE | `/api/artistas/{id}` | Elimina un artista |

---

### Albums (requiere token)

#### Crear album

```
POST /api/artistas/{id}/albums
```

Formato: `multipart/form-data`

| Campo | Tipo | Descripcion |
|---|---|---|
| `nombre` | texto | Nombre del album |
| `portada` | archivo | Imagen de portada |

Respuesta:
```json
{
  "id": 1,
  "nombre": "Back in Black",
  "portadaUrl": "/archivos/albums/xxx.png",
  "artistaId": 2
}
```

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/albums` | Lista todos los albums |
| DELETE | `/api/albums/{id}` | Elimina un album |

---

### Generos (requiere token)

#### Crear genero

```
POST /api/generos
```

Body (JSON):
```json
{
  "nombre": "Rock"
}
```

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/generos` | Lista todos los generos |
| DELETE | `/api/generos/{id}` | Elimina un genero |

---

### Playlists (requiere token)

#### Crear playlist

```
POST /api/listas
```

Body (JSON):
```json
{
  "nombre": "Road Trip",
  "idUsuario": 1
}
```

#### Añadir cancion a playlist

```
POST /api/listas/{id}/canciones
```

Body (JSON):
```json
{
  "idCancion": 5
}
```

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/listas/{id}/canciones` | Ver canciones de una playlist |
| DELETE | `/api/listas/{id}` | Elimina una playlist |

---

### Archivos APK y QR (requiere token)

Estos endpoints permiten subir, listar, descargar y eliminar archivos APK y codigos QR.

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/apk` | Sube un archivo APK (form-data, campo `apk`) |
| GET | `/api/apk` | Lista los APKs disponibles |
| GET | `/api/apk/{nombre}` | Descarga un APK concreto |
| DELETE | `/api/apk/{nombre}` | Elimina un APK |
| POST | `/api/qr` | Sube una imagen QR (form-data, campo `qr`) |
| GET | `/api/qr` | Lista los QRs disponibles |
| GET | `/api/qr/{nombre}` | Descarga un QR concreto |
| DELETE | `/api/qr/{nombre}` | Elimina un QR |

---

## Nginx como reverse proxy (opcional)

Si se quiere exponer la API a traves de Nginx en el puerto 90, hay que crear el archivo de configuracion `/etc/nginx/sites-available/ktor-api`:

```nginx
server {
    listen 90;
    server_name _;

    location / {
        proxy_pass http://localhost:8001;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

Activar la configuracion:

```bash
sudo ln -s /etc/nginx/sites-available/ktor-api /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

El flujo de la peticion seria: `Cliente -> Nginx (puerto 90) -> API Ktor (puerto 8001)`.

---

## Despliegue en Raspberry Pi

La imagen Docker esta compilada para arquitectura **ARM 32-bit** (`linux/arm/v7`), que es la arquitectura de la Raspberry Pi 3.

Los scripts de ayuda incluidos son:

- `deploy.sh` - Construye la imagen y la publica en Docker Hub
- `actualizar.sh` - Descarga la ultima imagen y reinicia los contenedores en la Raspberry

Para desplegar manualmente:

```bash
# En la Raspberry, descargar y arrancar
docker compose pull
docker compose up -d
```

---

## Errores comunes

| Codigo | Significado |
|---|---|
| `400 Bad Request` | Los datos enviados son incorrectos o faltan campos |
| `401 Unauthorized` | No se ha enviado el token o es invalido |
| `404 Not Found` | El recurso solicitado no existe |
| `500 Internal Server Error` | Error interno del servidor |

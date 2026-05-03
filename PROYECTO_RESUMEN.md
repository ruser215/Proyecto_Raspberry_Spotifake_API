# Contexto del Proyecto: Spotifake API

Este documento sirve como referencia técnica para optimizar la interacción con el asistente, reduciendo el consumo de tokens y asegurando respuestas precisas.

## 🛠 Arquitectura y Tecnologías
- **Framework**: Ktor (Kotlin)
- **Base de Datos**: Exposed (SQL)
- **Serialización**: `kotlinx.serialization` (JSON)
- **Autenticación**: JWT (Bearer Token)
- **Configuración**: Variables de entorno en `.env` (JWT_SECRET, JWT_DOMAIN, etc.)

## 🌐 Red y Despliegue
- **Base URL**: `https://ruser215.freedynamicdns.org/api`
- **Puerto Local**: `8001`
- **Root Path**: `/api` (Configurado en `application.yaml`)

## 🔑 Seguridad (JWT)
- **Configuración**: `Security.kt` -> realm="auth-jwt"
- **Claims en el Token**:
    - `id` (Long): ID único del usuario.
    - `correo` (String): Email del usuario.
    - `admin` (Int): `1` para Administrador, `0` para Usuario Normal.
    - `premium` (Int): `1` para Premium, `0` para Gratuito.
- **Validación**: El token enviado debe coincidir con el campo `token` almacenado en la tabla `usuarios` (vía `repository.getUsuarioByCorreo`).

## 📂 Modelos Críticos y Reglas de Serialización
- **`UpdateUsuario`**: Todos los campos son nulables (`String?`, `Boolean?`) y tienen valor por defecto `null`. Es fundamental para permitir peticiones `PATCH` parciales.
- **`Usuario`**: Modelo principal. El campo `pass` se devuelve vacío (`""`) en peticiones GET por seguridad.
- **`Genero`**: Posee `id: Int = 0` por defecto para facilitar la creación (`POST`).

## 🚦 Reglas de Negocio y Permisos (RBAC)

### Usuarios (`/usuarios`)
- **Listar/Ver**: Cualquier usuario autenticado puede listar (`GET /usuarios`) o ver perfiles.
- **Filtrado**: `GET /usuarios?username=...` filtra por nombre exacto.
- **Edición**:
    - Usuario normal: Solo su propio perfil. No puede cambiar su estado `admin` o `premium`.
    - Admin: Puede editar cualquier usuario y cambiar roles.

### Géneros, Canciones, Artistas y Álbumes
- **Lectura**: Público/Autenticado.
- **Escritura (POST/PATCH/DELETE)**: **Solo Administradores**.

### Playlists (`/listas`)
- **Escritura**: El usuario creador o Administradores.

### Archivos Estáticos
- **Rutas físicas**: `/archivos/qr`, `/archivos/apk`, `/archivos/portadas`, `/archivos/audio`, `/archivos/perfiles`.
- **Acceso**: `GET /apk/{nombre}` y `GET /qr/{nombre}` son públicos.

## 📝 Notas para el Desarrollador/IA
1.  **PATCH**: Siempre usar `call.receive<UpdateUsuario>()` y verificar valores `null` antes de actualizar en DB.
2.  **404**: Recordar que el prefijo `/api` es obligatorio en todas las rutas.
3.  **Exposed**: Las consultas a la DB deben ejecutarse dentro de `suspendTransaction`.

## 🔄 Historial de Cambios Relevantes

### 2026-05-03 — Refactor modelo `Cancion` (relaciones N:M)
- **`Cancion.kt`** (modelo de dominio): Cambiado a relaciones N:M para artistas y géneros:
  - `artistaId: Int?` → `artistaIds: List<Int>` (lista de IDs de artista)
  - `albumIds: List<Int>?` eliminado → `albumId: Int?` se mantiene (solo un álbum)
  - `generosIds: List<Int>` añadido (lista de IDs de género; `genero: Int?` queda como campo informativo)
  - `@Serializable` eliminado temporalmente (no era necesario con Ktor content negotiation)
- **`CancionInterface.kt`**: `updateCancion` ampliado con `artistaIds`, `albumIds`, `generosIds` como parámetros opcionales.
- **`CancionDao.kt`**: `toCancion()` actualizado — `artistaIds = listOfNotNull(artista?.id?.value)`, `generosIds = listOfNotNull(genero.value)`.
- **`PersistenceCancionRepository.kt`**: `updateCancion` actualizado con todos los parámetros de la interfaz; `createCancion` usa `artistaIds.firstOrNull()` para la FK de artista.
- **`Routing.kt`**: Línea `albumIds = albumIds` corregida a `albumId = albumIds.firstOrNull()` al construir el objeto `Cancion`.


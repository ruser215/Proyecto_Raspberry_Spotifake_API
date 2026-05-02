# 🎵 Spotifake API - Guía Completa de Endpoints

**Base URL:** `https://ruser215.freedynamicdns.org/api`  
*Nota: Todas las rutas protegidas requieren el prefijo `/api` y el token Bearer en el Header `Authorization: Bearer <token>`.*

---

## 🔐 Autenticación y Registro

### Registro de Usuario
- **Endpoint:** `POST /register`
- **Formato:** `JSON`
- **Campos:**
    - `username` (String, **Obligatorio**): Nombre de usuario.
    - `correo` (String, **Obligatorio**): Email único.
    - `pass` (String, **Obligatorio**): Contraseña.
- **Ejemplo:**
```json
{
  "username": "tomas",
  "correo": "tomas@red.com",
  "pass": "1234"
}
```

### Login
- **Endpoint:** `POST /login`
- **Formato:** `JSON`
- **Campos:**
    - `correo` (String, **Obligatorio**)
    - `pass` (String, **Obligatorio**)
- **Ejemplo:**
```json
{
  "correo": "tomas@red.com",
  "pass": "1234"
}
```

---

## 👥 Usuarios (Protegido)

### Listar Usuarios
- **Endpoint:** `GET /usuarios`
- **Query Params:** `?username=xxx` (Opcional: filtra por coincidencia exacta).

### Actualizar Perfil o Roles (PATCH)
- **Endpoint:** `PATCH /usuarios/{id}`
- **Formato:** `JSON`
- **Campos (Todos Opcionales):**
    - `username` (String): Nuevo nombre visual.
    - `correo` (String): Nuevo email.
    - `pass` (String): Nueva contraseña (se hashea automáticamente).
    - `admin` (Boolean, **Solo Admin**): true o false.
    - `premium` (Boolean, **Solo Admin**): true o false.
- **Ejemplo:**
```json
{
  "username": "tomas_pro",
  "premium": true
}
```

### Cambiar Foto de Perfil
- **Endpoint:** `PATCH /usuarios/{id}/perfil`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos:**
    - `imagen` (**Archivo**, **Obligatorio**): El archivo de imagen (JPG, PNG).

---

## 🎤 Artistas (Admin solo escritura)

### Crear Artista
- **Endpoint:** `POST /artistas`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos:**
    - `nombre` (Texto, **Obligatorio**): Nombre del artista.
    - `foto` (**Archivo**, Opcional): Imagen del artista.

### Editar Artista
- **Endpoint:** `PATCH /artistas/{id}`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos (Opcionales):**
    - `nombre` (Texto): Nuevo nombre.
    - `foto` (**Archivo**): Nueva imagen.

---

## 💿 Álbumes (Admin solo escritura)

### Crear Álbum
- **Endpoint:** `POST /artistas/{idArtista}/albums`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos:**
    - `nombre` (Texto, **Obligatorio**): Título del álbum.
    - `portada` (**Archivo**, Opcional): Imagen de carátula.

### Editar Álbum
- **Endpoint:** `PATCH /albums/{id}`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos (Opcionales):**
    - `nombre` (Texto)
    - `artistaId` (Número): ID del artista dueño para mover el álbum.
    - `portada` (**Archivo**)

---

## 🎵 Canciones (Admin solo escritura)

### Crear Canción
- **Endpoint:** `POST /canciones`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos:**
    - `nombre` (Texto, **Obligatorio**): Título de la pista.
    - `audio` (**Archivo**, **Obligatorio**): Archivo MP3 o WAV.
    - `portada` (**Archivo**, Opcional): Imagen específica de la canción.
    - `artista` (Texto, Opcional): Nombre visual (string).
    - `album` (Texto, Opcional): Nombre visual (string).
    - `artistaId` (Número, Opcional): ID real del artista en DB.
    - `albumId` (Número, Opcional): ID real del álbum en DB.
    - `genero` (Número, Opcional): ID del género.
    - `likes` (Número, Opcional): Contador inicial.

### Editar Canción
- **Endpoint:** `PATCH /canciones/{id}`
- **Formato:** `multipart/form-data` (Form-Data)
- **Campos:** Cualquiera de los anteriores de forma opcional.

---

## 🎸 Géneros (Admin solo escritura)

### Crear Género
- **Endpoint:** `POST /generos`
- **Formato:** `JSON`
- **Campos:**
    - `nombre` (String, **Obligatorio**)
- **Ejemplo:** `{"nombre": "Rock"}`

### Editar Género
- **Endpoint:** `PATCH /generos/{id}`
- **Formato:** `JSON`
- **Campos:**
    - `nombre` (String, **Obligatorio**)

---

## 📜 Playlists (Listas)

### Crear Lista
- **Endpoint:** `POST /listas`
- **Formato:** `JSON`
- **Campos:**
    - `nombre` (String, **Obligatorio**)
    - `idUsuario` (Número, **Obligatorio**): ID del usuario dueño.
- **Ejemplo:** `{"nombre": "Mis Favoritas", "idUsuario": 1}`

### Añadir Canción a Lista
- **Endpoint:** `POST /listas/{id}/canciones`
- **Formato:** `JSON`
- **Campos:**
    - `idCancion` (Número, **Obligatorio**)
- **Ejemplo:** `{"idCancion": 10}`

---

## 🎧 Reproducciones y Letras

### Registrar Reproducción
- **Endpoint:** `POST /reproducir`
- **Formato:** `JSON`
- **Campos:**
    - `idCancion` (Número, **Obligatorio**)
    - `fecha` (String, Opcional): Ejemplo `"2023-10-27"`.

### Letras
- **Obtener:** `GET /lyrics/{cancionId}`

---

## 🐾 Mascotas y Alarmas

### Mascotas
- **Comprar:** `POST /mascotas/buy/{mascotaId}`
- **Activar:** `POST /mascotas/active/{mascotaId}`

### Alarmas
- **Crear:** `POST /alarms`
- **Formato:** `JSON`
- **Campos:**
    - `hora` (String, **Obligatorio**): Formato `"HH:mm"`.
    - `cancionId` (Número, **Obligatorio**)
    - `dias` (String, Opcional): Ejemplo `"L,M,X"`.
    - `activa` (Boolean, Opcional): `true` / `false`.

---

## 📁 Archivos (Público y Admin)

### Descargas (Público)
- `GET /apk/{nombre}`
- `GET /qr/{nombre}`

### Gestión Admin (Solo Admin)
- **Subir QR:** `POST /qr` -> Form-Data, campo `qr` (**Archivo**).
- **Subir APK:** `POST /apk` -> Form-Data, campo `apk` (**Archivo**).
- **Eliminar:** `DELETE /qr/{nombre}` o `DELETE /apk/{nombre}`.

import re
import uuid
import json
from datetime import datetime

sql = """
CREATE TABLE Usuario (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  correo VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  admin BOOLEAN DEFAULT FALSE,
  premium BOOLEAN DEFAULT FALSE,
  urlImagen TEXT,
  fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  temaId INT
);

CREATE TABLE Tema (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(50),
  colorPrimario VARCHAR(20),
  colorSecundario VARCHAR(20),
  colorFondo VARCHAR(20),
  modoOscuro BOOLEAN
);

CREATE TABLE BusquedaUsuario (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  textoBusqueda TEXT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TimerDormir (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  hora TIME,
  activo BOOLEAN
);

CREATE TABLE Alarma (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  cancionId INT,
  hora TIME,
  activo BOOLEAN
);

CREATE TABLE Genero (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(50)
);

CREATE TABLE Artista (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  fotoUrl TEXT
);

CREATE TABLE Album (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  portadaUrl TEXT,
  fechaLanzamiento DATE
);

CREATE TABLE Cancion (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(150),
  duracionSegundos INT,
  generoId INT,
  reproducciones BIGINT DEFAULT 0,
  youtubeId VARCHAR(50),
  urlPortada TEXT,
  isrc VARCHAR(20)
);

CREATE TABLE SongArtists (
  songId INT,
  artistId INT,
  PRIMARY KEY (songId, artistId)
);

CREATE TABLE SongAlbums (
  songId INT,
  albumId INT,
  numeroTrack INT,
  PRIMARY KEY (songId, albumId)
);

CREATE TABLE AlbumArtists (
  albumId INT,
  artistId INT,
  PRIMARY KEY (albumId, artistId)
);

CREATE TABLE Playlist (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100),
  usuarioId BIGINT,
  publica BOOLEAN,
  tokenCompartir VARCHAR(100),
  fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PlaylistCancion (
  playlistId BIGINT,
  cancionId INT,
  orden INT,
  fechaAñadido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (playlistId, cancionId)
);

CREATE TABLE PlaylistColaborador (
  playlistId BIGINT,
  usuarioId BIGINT,
  rol VARCHAR(10),
  PRIMARY KEY (playlistId, usuarioId)
);

CREATE TABLE SolicitudColaboracionPlaylist (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  playlistId BIGINT,
  usuarioInvitadoId BIGINT,
  usuarioInvitadorId BIGINT,
  estado VARCHAR(20),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE FollowArtista (
  usuarioId BIGINT,
  artistaId INT,
  fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (usuarioId, artistaId)
);

CREATE TABLE LikedSongs (
  usuarioId BIGINT,
  cancionId INT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (usuarioId, cancionId)
);

CREATE TABLE Reproduccion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  cancionId INT,
  fechaHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  duracionEscuchada INT
);

CREATE TABLE SolicitudAmistad (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  remitenteId BIGINT,
  destinatarioId BIGINT,
  estado VARCHAR(20),
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Amistad (
  usuario1Id BIGINT,
  usuario2Id BIGINT,
  fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (usuario1Id, usuario2Id)
);

CREATE TABLE Letra (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cancionId INT,
  fuente VARCHAR(50),
  fechaActualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE LetraSync (
  id INT PRIMARY KEY AUTO_INCREMENT,
  letraId INT,
  tiempoMs INT,
  texto VARCHAR(255),
  orden INT
);

CREATE TABLE Mascota (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(50),
  tipoAnimal VARCHAR(50),
  urlSprite TEXT,
  precio DOUBLE,
  premiumDefault BOOLEAN
);

CREATE TABLE UsuarioMascota (
  usuarioId BIGINT,
  mascotaId INT,
  activa BOOLEAN,
  fechaCompra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (usuarioId, mascotaId)
);

CREATE TABLE Anuncio (
  id INT PRIMARY KEY AUTO_INCREMENT,
  titulo VARCHAR(100),
  urlImagen TEXT,
  urlDestino TEXT,
  duracionSegundos INT,
  activo BOOLEAN
);

CREATE TABLE AnuncioVisualizacion (
  id INT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  anuncioId INT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Notificacion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  tipo VARCHAR(50),
  mensaje TEXT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  leida BOOLEAN
);

CREATE TABLE Recomendacion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuarioId BIGINT,
  cancionId INT,
  score DOUBLE,
  fechaGenerada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ResumenAnual (
  usuarioId BIGINT,
  año INT,
  cancionTopId INT,
  artistaTopId INT,
  minutosEscuchados INT,
  PRIMARY KEY (usuarioId, año)
);
"""

# Hardcoded Foreign Keys logic roughly based on the fields since I stripped CONSTRAINTS to simplify regex.
foreign_keys = [
    ("Usuario", "temaId", "Tema"),
    ("BusquedaUsuario", "usuarioId", "Usuario"),
    ("TimerDormir", "usuarioId", "Usuario"),
    ("Alarma", "usuarioId", "Usuario"),
    ("Alarma", "cancionId", "Cancion"),
    ("Cancion", "generoId", "Genero"),
    ("SongArtists", "songId", "Cancion"),
    ("SongArtists", "artistId", "Artista"),
    ("SongAlbums", "songId", "Cancion"),
    ("SongAlbums", "albumId", "Album"),
    ("AlbumArtists", "albumId", "Album"),
    ("AlbumArtists", "artistId", "Artista"),
    ("Playlist", "usuarioId", "Usuario"),
    ("PlaylistCancion", "playlistId", "Playlist"),
    ("PlaylistCancion", "cancionId", "Cancion"),
    ("PlaylistColaborador", "playlistId", "Playlist"),
    ("PlaylistColaborador", "usuarioId", "Usuario"),
    ("SolicitudColaboracionPlaylist", "playlistId", "Playlist"),
    ("SolicitudColaboracionPlaylist", "usuarioInvitadoId", "Usuario"),
    ("SolicitudColaboracionPlaylist", "usuarioInvitadorId", "Usuario"),
    ("FollowArtista", "usuarioId", "Usuario"),
    ("FollowArtista", "artistaId", "Artista"),
    ("LikedSongs", "usuarioId", "Usuario"),
    ("LikedSongs", "cancionId", "Cancion"),
    ("Reproduccion", "usuarioId", "Usuario"),
    ("Reproduccion", "cancionId", "Cancion"),
    ("SolicitudAmistad", "remitenteId", "Usuario"),
    ("SolicitudAmistad", "destinatarioId", "Usuario"),
    ("Amistad", "usuario1Id", "Usuario"),
    ("Amistad", "usuario2Id", "Usuario"),
    ("Letra", "cancionId", "Cancion"),
    ("LetraSync", "letraId", "Letra"),
    ("UsuarioMascota", "usuarioId", "Usuario"),
    ("UsuarioMascota", "mascotaId", "Mascota"),
    ("AnuncioVisualizacion", "usuarioId", "Usuario"),
    ("AnuncioVisualizacion", "anuncioId", "Anuncio"),
    ("Notificacion", "usuarioId", "Usuario"),
    ("Recomendacion", "usuarioId", "Usuario"),
    ("Recomendacion", "cancionId", "Cancion"),
    ("ResumenAnual", "usuarioId", "Usuario"),
    ("ResumenAnual", "cancionTopId", "Cancion"),
    ("ResumenAnual", "artistaTopId", "Artista"),
]

def parse_sql(sql_text):
    tables = []
    # match CREATE TABLE name ( block )
    for m in re.finditer(r'CREATE TABLE (\w+) \((.*?)\);', sql_text, re.DOTALL):
        tname = m.group(1)
        body = m.group(2)
        columns = []
        pks = set()
        for line in body.split(','):
            line = line.strip()
            if not line: continue
            if line.startswith("PRIMARY KEY"):
                # PRIMARY KEY (a, b)
                pk_match = re.search(r'\((.*?)\)', line)
                if pk_match:
                    for k in pk_match.group(1).split(','):
                        pks.add(k.strip())
            elif not line.startswith("FOREIGN KEY"): # Ignore foreign keys if any leaked
                parts = line.split()
                if not parts: continue
                cname = parts[0]
                datatype = " ".join(parts[1:])
                is_pk = "PRIMARY KEY" in datatype or cname in pks
                if cname in pks: is_pk = True
                columns.append({
                    "name": cname,
                    "type": parts[1] if len(parts)>1 else "VARCHAR",
                    "pk": is_pk,
                    "notNull": "NOT NULL" in datatype,
                    "autoInc": "AUTO_INCREMENT" in datatype
                })
        tables.append({"name": tname, "columns": columns})
    return tables

tables_def = parse_sql(sql)

document = {
    "documentName": "Spotifake",
    "lastUpdatedAt": datetime.utcnow().isoformat() + "Z",
    "tableViewModels": [],
    "columnGroupModels": [],
    "columnModels": [],
    "relationshipModels": []
}

col_id_map = {} # (tname, cname) -> col_uuid
table_id_map = {} # tname -> table_uuid

grid_cols = 5
grid_x = 100
grid_y = 100

for i, t in enumerate(tables_def):
    tid = str(uuid.uuid4())
    table_id_map[t["name"]] = tid
    col_ids = []
    for c in t["columns"]:
        cid = str(uuid.uuid4())
        col_id_map[(t["name"], c["name"])] = cid
        col_ids.append(cid)
        
        # Add column model
        col_data = {
            "columnModelId": cid,
            "columnShareModelId": str(uuid.uuid4()), # Need unique per column
            "physicalName": c["name"],
            "logicalName": c["name"],
            "dataType": c["type"],
            "primaryKey": c["pk"],
            "notNull": c["notNull"],
            "autoIncrement": c["autoInc"]
        }
        document["columnModels"].append(col_data)

    r = i // grid_cols
    c = i % grid_cols
    
    tvm = {
        "tableModel": {
            "tableModelId": tid,
            "physicalName": t["name"],
            "logicalName": t["name"],
            "columnModelIds": col_ids
        },
        "top": r * 200,
        "left": c * 250,
        "headerBackgroundColor": { "red": 227, "green": 242, "blue": 253 },
        "headerForegroundColor": { "red": 0, "green": 0, "blue": 0 },
        "createdAt": datetime.utcnow().isoformat() + "Z"
    }
    document["tableViewModels"].append(tvm)

for pk_t, fk_c, target_t in foreign_keys:
    if target_t in table_id_map and pk_t in table_id_map:
        # Find PK column in target_t
        target_t_def = next((td for td in tables_def if td["name"] == target_t), None)
        if not target_t_def: continue
        target_col = next((c for c in target_t_def["columns"] if c["pk"]), None)
        if not target_col: continue

        end_col_id = col_id_map.get((pk_t, fk_c))
        start_col_id = col_id_map.get((target_t, target_col["name"]))

        if end_col_id and start_col_id:
            rel = {
                "relationshipModelId": str(uuid.uuid4()),
                "relationshipType": "OneN",
                "start": {
                    "tableModelId": table_id_map[target_t],
                    "columnModelIds": [start_col_id]
                },
                "end": {
                    "tableModelId": table_id_map[pk_t],
                    "columnModelIds": [end_col_id]
                }
            }
            document["relationshipModels"].append(rel)

with open('db/diseño.erd', 'w') as f:
    json.dump(document, f, indent=4)
print("ERD generated successfully!")

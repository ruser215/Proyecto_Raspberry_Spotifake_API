#!/usr/bin/env bash

set -e  # Detener si algo falla

echo "========================================="
echo " Iniciando actualización de la API"
echo "========================================="

echo "Arquitectura detectada: $(uname -m)"
echo "Compilación nativa en Raspberry 32 bits (sin buildx)"

# --- Comprobar que estamos en repo git ---

if [ ! -d ".git" ]; then
echo "Este directorio no es un repositorio Git"
exit 1
fi

# --- Actualizar código ---

echo "Descargando cambios de GitHub..."
git fetch origin
git pull --rebase origin main

# --- Compilar JAR en la Raspberry ---

if ! command -v java >/dev/null 2>&1; then
	echo "Java no está instalado en la Raspberry."
	echo "Instálalo con: sudo apt update && sudo apt install -y openjdk-17-jdk"
	exit 1
fi

chmod +x ./gradlew
echo "Compilando fat JAR localmente..."
./gradlew clean buildFatJar --no-daemon

# --- Actualizar contenedor API ---

echo "Actualizando contenedores usando el JAR compilado localmente..."
docker compose down || true
docker compose build --no-cache --pull ktor-app
docker compose up -d --force-recreate

# --- Limpiar imágenes antiguas ---

echo "Limpiando imágenes y caché..."
docker image prune -f
docker builder prune -f

echo "Actualización completada"
echo "👉 Base de datos y servicios auxiliares intactos"
echo "========================================="
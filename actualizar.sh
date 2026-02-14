#!/usr/bin/env bash

set -e  # Detener si algo falla

echo "========================================="
echo " Iniciando actualización de la API"
echo "========================================="

# --- Comprobar que estamos en repo git ---

if [ ! -d ".git" ]; then
echo "Este directorio no es un repositorio Git"
exit 1
fi

# --- Actualizar código ---

echo "Descargando cambios de GitHub..."
git fetch origin
git pull --rebase origin main

# --- Actualizar contenedor API ---

echo "Actualizando contenedores (sin cache)..."
docker compose build --no-cache ktor-app
docker compose up -d --force-recreate

# --- Limpiar imágenes antiguas ---

echo "Limpiando imágenes y caché..."
docker image prune -f
docker builder prune -f

echo "Actualización completada"
echo "👉 Base de datos y servicios auxiliares intactos"
echo "========================================="
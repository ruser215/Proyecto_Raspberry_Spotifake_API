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

# --- Reconstruir contenedor API ---

echo "Reconstruyendo contenedor Ktor..."
docker compose up -d --build ktor-app

# --- Limpiar imágenes antiguas ---

echo "Limpiando imágenes colgantes..."
docker image prune -f

echo "Actualización completada"
echo "👉 Base de datos y servicios auxiliares intactos"
echo "========================================="

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

# --- Construir y subir imagen en la Raspberry ---

IMAGE_NAME="${IMAGE_NAME:-ruser215/api-spotifake}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
PLATFORM="${PLATFORM:-linux/arm/v7}"

echo "Construyendo imagen ($PLATFORM)..."
docker build --platform "$PLATFORM" -t "$IMAGE_NAME:$IMAGE_TAG" .

echo "Subiendo imagen a Docker Hub..."
docker push "$IMAGE_NAME:$IMAGE_TAG"

# --- Actualizar contenedor API ---

echo "Actualizando contenedor Ktor (con imagen remota)..."
docker compose up -d ktor-app

# --- Limpiar imágenes antiguas ---

echo "Limpiando imágenes colgantes..."
docker image prune -f

echo "Actualización completada"
echo "👉 Base de datos y servicios auxiliares intactos"
echo "========================================="

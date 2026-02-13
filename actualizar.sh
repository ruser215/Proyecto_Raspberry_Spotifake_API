#!/bin/bash

echo "Iniciando actualización de la API..."

# 1. Bajamos los últimos cambios del repositorio
echo "Descargando cambios de GitHub..."
git pull origin main

# 2. Reconstruimos solo la API sin tocar la DB ni PHPMyAdmin
echo "Reconstruyendo contenedor de Ktor (ktor-app)..."
docker-compose up -d --build ktor-app

echo "¡Actualización completada! La base de datos y adminer no han sido reiniciados."
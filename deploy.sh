#!/bin/bash

# Script para desplegar la API Ktor en Raspberry Pi
# Uso: ./deploy.sh

set -e

echo "🚀 Desplegando API Ktor Spotifake..."

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker no está instalado${NC}"
    echo "Instala Docker con: curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh"
    exit 1
fi

# Verificar que Docker Compose esté disponible
if ! docker compose version &> /dev/null; then
    echo -e "${RED}❌ Docker Compose no está disponible${NC}"
    echo "Instala Docker Compose plugin"
    exit 1
fi

echo -e "${YELLOW}📦 Deteniendo contenedores existentes...${NC}"
docker compose down || true

echo -e "${YELLOW}🔨 Construyendo imagen de Ktor...${NC}"
docker compose build ktor-app

echo -e "${YELLOW}🚀 Iniciando contenedores...${NC}"
docker compose up -d

echo -e "${YELLOW}⏳ Esperando a que los servicios estén listos...${NC}"
sleep 10

# Verificar que los contenedores estén corriendo
if docker ps | grep -q "ktor-app"; then
    echo -e "${GREEN}✅ Ktor API está corriendo${NC}"
else
    echo -e "${RED}❌ Error: Ktor API no se inició correctamente${NC}"
    echo "Ver logs con: docker logs ktor-app"
    exit 1
fi

if docker ps | grep -q "ktor-db"; then
    echo -e "${GREEN}✅ MySQL está corriendo${NC}"
else
    echo -e "${RED}❌ Error: MySQL no se inició correctamente${NC}"
    echo "Ver logs con: docker logs ktor-db"
    exit 1
fi

echo ""
echo -e "${GREEN}🎉 Despliegue completado exitosamente!${NC}"
echo ""
echo "📊 Estado de los servicios:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "🔗 URLs disponibles:"
echo "  - API Ktor:    http://localhost:8001"
echo "  - Adminer:     http://localhost:8081"
echo "  - Nginx:       http://localhost:90 (si está configurado)"
echo ""
echo "📝 Comandos útiles:"
echo "  - Ver logs de Ktor:  docker logs -f ktor-app"
echo "  - Ver logs de MySQL: docker logs -f ktor-db"
echo "  - Detener todo:      docker compose down"
echo "  - Reiniciar:         docker compose restart"
echo ""
echo "🧪 Probar la API:"
echo "  curl http://localhost:8001/"

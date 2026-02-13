## Configuración de Nginx para Reverse Proxy

Si Nginx está escuchando en el puerto 90 y redirigiendo al 8001, necesitas esta configuración:

### Archivo de configuración de Nginx

**Ubicación:** `/etc/nginx/sites-available/ktor-api`

```nginx
server {
    listen 90;
    server_name _;

    # Logs
    access_log /var/log/nginx/ktor-api-access.log;
    error_log /var/log/nginx/ktor-api-error.log;

    # Proxy a la API Ktor
    location / {
        proxy_pass http://localhost:8001;
        proxy_http_version 1.1;
        
        # Headers necesarios
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Cache bypass
        proxy_cache_bypass $http_upgrade;
    }
}
```

### Activar la configuración

```bash
# Crear enlace simbólico
sudo ln -s /etc/nginx/sites-available/ktor-api /etc/nginx/sites-enabled/

# Verificar configuración
sudo nginx -t

# Recargar Nginx
sudo systemctl reload nginx
```

### Verificar que Nginx esté escuchando en el puerto 90

```bash
sudo netstat -tlnp | grep :90
```

### Arquitectura del flujo

```
Internet → ngrok → Puerto 90 (Nginx) → Puerto 8001 (Ktor API en Docker)
```

### Comandos para desplegar

```bash
# 1. Construir e iniciar contenedores
docker compose up -d --build

# 2. Verificar que estén corriendo
docker ps

# 3. Ver logs de Ktor
docker logs -f ktor-app

# 4. Ver logs de MySQL
docker logs -f ktor-db
```

### Verificar conectividad

```bash
# Desde la Raspberry, probar la API directamente
curl http://localhost:8001/

# Probar a través de Nginx
curl http://localhost:90/

# Ver logs de Nginx
sudo tail -f /var/log/nginx/ktor-api-access.log
```

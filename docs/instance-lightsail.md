# Documentación de Implementación: IWVG

Guía de sugerencia de instalación de instancia

## 1️⃣ Crear instancia AWS LightSail

### Especificaciones
- Operating system (OS) only: Ubuntu 22.04 LTS
- General purpose
- 1GB RAM (preferable 2GB) (First 90 days free)
- Name: iwvg

### Firewall. Abrir (IPv4 & IPv6)
- SSH TCP 22 (Lightsail browser SSH)
- HTTP TCP 80 (Any IPv4 address)
- HTTPS TCP 443 (Any IPv4 address)

## 2️⃣ Preparar servidor Ubuntu

### Verificación del sistema
```bash
lsb_release -a
```
### Actualización del sistema
```bash
sudo apt update && sudo apt upgrade -y
sudo reboot
```

## 3️⃣ Instalación de Docker
```bash
sudo apt update
sudo apt install -y docker.io 
sudo usermod -aG docker $USER
newgrp docker
```

### Verificación de la versión de Docker
```bash
docker --version
```

### Instalación de Docker Compose v2
```bash
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
```

### Verificación de la versión de Docker Compose
```bash
docker compose version
```

### Configuración de red Docker
```bash
# Crear red
docker network create goa

# Verificar redes
docker network ls

# Inspeccionar red
docker network inspect goa
```

## 4️⃣ Optimización del Servidor

### Verificación de memoria disponible
```bash
free -h
```

### Configuración de memoria virtual (swap)
```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### Hacer el swap permanente
```bash
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

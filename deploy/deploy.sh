#!/bin/bash

# VibeCommerce Deployment Scripts

set -e

echo "=== VibeCommerce Deployment ==="

# 1. Build Backend
echo "[1/5] Building backend..."
cd backend
mvn clean package -DskipTests
cd ../..

# 2. Build Docker Images
echo "[2/5] Building Docker images..."
docker build -t vibecoding/gateway:latest -f deploy/Dockerfile backend/gateway
docker build -t vibecoding/product-service:latest -f deploy/Dockerfile backend/product-service
docker build -t vibecoding/user-service:latest -f deploy/Dockerfile backend/user-service
docker build -t vibecoding/order-service:latest -f deploy/Dockerfile backend/order-service
docker build -t vibecoding/cms-service:latest -f deploy/Dockerfile backend/cms-service

# 3. Initialize Database
echo "[3/5] Starting MySQL and initializing..."
docker-compose up -d mysql
sleep 10
docker exec -i vibecoding-mysql mysql -uroot -proot123 ecommerce < docs/02-ddl.sql

# 4. Start All Services
echo "[4/5] Starting all services..."
docker-compose up -d

# 5. Check Status
echo "[5/5] Checking services..."
sleep 5
docker-compose ps

echo "=== Deployment Complete ==="
echo "Gateway: http://localhost:8080"
echo "Nginx: http://localhost"
echo ""
echo "Initial Admin Account:"
echo "  Username: admin"
echo "  Password: admin123"
#!/bin/bash

# 设置 Java 17
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.15/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "Using Java:"
java -version

cd /Users/answer/dev/code/vibecoding/vibecoding-site/backend

# 先杀掉占用的进程
for port in 8080 8081 8082 8083; do
  pid=$(lsof -t -i :$port 2>/dev/null)
  if [ -n "$pid" ]; then
    echo "Killing process on port $port (PID: $pid)"
    kill -9 $pid 2>/dev/null
  fi
done

sleep 2

echo "Starting services..."

# 启动 product-service
echo "Starting product-service..."
nohup java -jar product-service/target/product-service-1.0.0-SNAPSHOT.jar > /tmp/product.log 2>&1 &
sleep 5

# 启动 user-service
echo "Starting user-service..."
nohup java -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar > /tmp/user.log 2>&1 &
sleep 5

# 启动 order-service
echo "Starting order-service..."
nohup java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar > /tmp/order.log 2>&1 &
sleep 5

echo ""
echo "=== Service Status ==="
lsof -i :8081 | head -2
lsof -i :8082 | head -2
lsof -i :8083 | head -2

echo ""
echo "=== Testing API ==="
curl -s http://localhost:8081/api/v1/products | head -100
# VibeCommerce Dockerfile 镜像
# 构建: cd backend && mvn clean package -DskipTests
# 运行: docker build -t vibecoding/gateway:latest -f gateway/Dockerfile .

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# ========================
# Runtime-only image
# 约定：在宿主机先完成构建，再打镜像
# - 后端产物：bigdata-server/target/*.jar
# ========================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 复制宿主机构建好的 jar 包
COPY bigdata-server/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]

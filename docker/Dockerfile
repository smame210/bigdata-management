FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY bigdata-server/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]

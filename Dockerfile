# ====== STAGE 1: build ======
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# copia pom y descarga dependencias en cache
COPY pom.xml .
RUN mvn -q -e -U -DskipTests dependency:go-offline

# copia el código
COPY src ./src
# compila (jar con dependencias)
RUN mvn -q -DskipTests package

# ====== STAGE 2: runtime ======
FROM eclipse-temurin:21-jre-alpine
# seguridad: crea usuario sin privilegios
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
# copia sólo el jar empacado (ajusta el nombre si cambia)
COPY --from=build /app/target/*.jar app.jar

# variables útiles
ENV TZ=America/Mazatlan \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseZGC" \
    SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

# expone puerto (documentación)
EXPOSE 8080

# healthcheck (requiere Actuator /health o tu endpoint)
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health | grep '"status":"UP"' || exit 1

USER spring
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

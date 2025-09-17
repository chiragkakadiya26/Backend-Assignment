# ======= Build stage =======
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Cache dependencies
COPY pom.xml ./
RUN mvn -q -B -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -B -DskipTests package

# ======= Runtime stage =======
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar
COPY --from=build /app/target/Backend-Assignment-0.0.1-SNAPSHOT.jar app.jar

# Environment configuration
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

# Heroku/Containers often pass PORT; default to 9090
ENV PORT=9090
EXPOSE 9090

# Use exec form; pass server.port and any JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]

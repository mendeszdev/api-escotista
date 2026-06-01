# ── Stage 1: build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Cache de dependências separado do código (rebuilds mais rápidos)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Compila e empacota
COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: runtime ─────────────────────────────────────────────────────────
FROM tomcat:10.1-jdk17-temurin-jammy

# Remove apps padrão do Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy como ROOT para que a URL fique /api/... em vez de /escoteiros-api/api/...
COPY --from=build /app/target/escoteiros-api.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]

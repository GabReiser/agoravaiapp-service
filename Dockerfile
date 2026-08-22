# syntax=docker/dockerfile:1

# ============================================================
# Stage 1: build (Maven + JDK 21)
# ============================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copia apenas o POM primeiro para aproveitar o cache de dependências
# entre builds (só rebaixa a camada quando o pom.xml mudar).
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copia o código-fonte e empacota no formato fast-jar do Quarkus
# (gera target/quarkus-app/quarkus-run.jar + lib/, app/ e quarkus/).
COPY src ./src
RUN mvn -B -DskipTests clean package

# ============================================================
# Stage 2: runtime (JRE 21, imagem enxuta)
# ============================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuário não-root por segurança.
RUN addgroup -S app && adduser -S app -G app

# O quarkus-run.jar referencia lib/, app/ e quarkus/ pelo Class-Path do
# manifesto, então precisamos copiar as quatro partes mantendo a estrutura.
COPY --from=builder --chown=app:app /build/target/quarkus-app/lib/ ./lib/
COPY --from=builder --chown=app:app /build/target/quarkus-app/*.jar ./
COPY --from=builder --chown=app:app /build/target/quarkus-app/app/ ./app/
COPY --from=builder --chown=app:app /build/target/quarkus-app/quarkus/ ./quarkus/

USER app

# Porta padrão do Core Service (quarkus.http.port=8082).
EXPOSE 8082

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar quarkus-run.jar"]

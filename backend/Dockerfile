FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /workspace
COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src
WORKDIR /workspace/backend
RUN mvn -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /workspace/backend/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Étape 1 : Construction du JAR avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copier pom.xml pour les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image finale légère
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copier le JAR généré
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Lancer l'application avec le port dynamique de Render
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=$PORT app.jar"]

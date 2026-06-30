# Dogs & Cats Microservices

Proyecto de microservicios desarrollado con **Spring Boot 4**, que expone APIs REST para gestionar perros y gatos, incluyendo integración entre servicios, testing completo, dockerización y pipeline CI.

---

## Tecnologías utilizadas

- Java 25
- Spring Boot 4
- Spring Web / WebFlux
- Spring Data JPA
- H2 Database
- Flyway
- MapStruct
- JUnit 5 / Mockito
- MockMvc
- JaCoCo (coverage)
- Docker & Docker Compose
- Azure Pipelines

---

## Arquitectura

El proyecto está compuesto por dos microservicios:

### Dogs Service
- Gestión de perros (CRUD)
- Consumo de API externa de chistes
- Exposición de endpoint de chistes consumidos por cats-service
- Consumo de `cats-service` para pokemons

### Cats Service
- Gestión de gatos (CRUD)
- Consumo de API externa de pokemons
- Exposición de endpoint de pokemons consumidos por dogs-service
- Consumo de `dogs-service` para chiste aleatorio en español

---

## Comunicación entre servicios

Los servicios se comunican vía HTTP usando `WebClient`:
dogs-service → cats-service
cats-service → dogs-service

Utilizan los nombres de contenedor Docker:
http://cats-service:8082
http://dogs-service:8081

---

## Ejecución con Docker

### Requisitos
- Docker
- Docker Compose

### Arranque

```bash
cd dogs-service
mvn clean package
cd ../cats-service
mvn clean package
cd ..
docker-compose up --build
```

### URLs principales
Dogs Service

Swagger: http://localhost:8081/swagger-ui.html

H2: http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:catsdb
User Name: sa

Cats Service

Swagger: http://localhost:8082/swagger-ui.html

H2: http://localhost:8082/h2-console
JDBC URL: jdbc:h2:mem:dogsdb
User Name: sa

### Testing
El proyecto incluye:

- Tests unitarios (services)
- Tests de integración (controllers)
- Mocking de WebClient
- Validación de errores HTTP
- Cobertura > 80% con JaCoCo

Ejecutar tests:

```bash
cd dogs-service
mvn clean test
cd ../cats-service
mvn clean test
```

### Cobertura

Cobertura total: 83%
Herramienta: JaCoCo

Informe generado en:
target/site/jacoco/index.html

### CI Pipeline
Incluye un pipeline básico de Azure Pipelines (azure-pipelines.yml) que:

Compila el proyecto
Ejecuta tests automáticamente

### Estructura del proyecto
dogs-service/
cats-service/
docker-compose.yml
azure-pipelines.yml

### Funcionalidades principales

CRUD de entidades
Validación de entrada (@Valid)
Manejo global de excepciones (@ControllerAdvice)
Llamadas a APIs externas
Comunicación entre microservicios
Dockerización completa

---

### Autor
Roumen Ivanov Andreev
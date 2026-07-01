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

- CRUD de perros
- Consumo de API externa de chistes
- Exposición de endpoint de chistes consumido por cats-service
- Consumo de cats-service para obtener pokemons
- Gestión de imágenes de perros

### Cats Service

- CRUD de gatos
- Consumo de API externa de pokemons
- Exposición de endpoint de pokemons consumido por dogs-service
- Consumo de dogs-service para obtener chistes
- Gestión de imágenes de gatos

## Comunicación entre servicios

Los servicios se comunican vía HTTP usando `WebClient`:
- dogs-service → cats-service
- cats-service → dogs-service

URLs internas Docker:

- http://cats-service:8082
- http://dogs-service:8081

## Requisitos previos

Antes de ejecutar el proyecto es necesario tener instalado:

- Java 25
- Apache Maven 3.9 o superior
- Docker
- Docker Compose

Comprobación rápida:

- java -version
- mvn -version
- docker --version
- docker compose version

## Ejecución en local

### Dogs Service

1. Entrar en la carpeta dogs-service
2. Ejecutar:

mvn clean spring-boot:run

URLs:

- API: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console

### Cats Service

1. Entrar en la carpeta cats-service
2. Ejecutar:

mvn clean spring-boot:run

URLs:

- API: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui/index.html
- H2 Console: http://localhost:8082/h2-console

## Ejecución con Docker

### Compilación

1. Entrar en dogs-service
2. Ejecutar mvn clean package
3. Entrar en cats-service
4. Ejecutar mvn clean package

### Arranque

Ejecutar:

docker-compose up --build

## URLs principales

### Dogs Service

- Swagger: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:dogsdb
- Usuario: sa

### Cats Service

- Swagger: http://localhost:8082/swagger-ui/index.html
- H2 Console: http://localhost:8082/h2-console
- JDBC URL: jdbc:h2:mem:catsdb
- Usuario: sa

## Gestión de imágenes

Los dos microservicios permiten asociar una imagen a cada entidad.

### Dogs

Subida de imagen:

POST /api/dogs/{id}/photo

Acceso a imagen:

http://localhost:8081/photos/dogs/{archivo}

Almacenamiento:

- dogs-service/uploads/dogs

### Cats

Subida de imagen:

POST /api/cats/{id}/photo

Acceso a imagen:

http://localhost:8082/photos/cats/{archivo}

Almacenamiento:

- cats-service/uploads/cats

## Ejecutar tests

### Dogs Service

mvn clean test

### Cats Service

mvn clean test

## Reporte de cobertura

El proyecto utiliza JaCoCo.

Generación:

mvn clean test

Informe:

target/site/jacoco/index.html

Cobertura actual:

- dogs-service: 82%
- cats-service: 80%

## CI Pipeline

Incluye azure-pipelines.yml para:

- Compilar el proyecto
- Ejecutar tests automáticamente

## Estructura del proyecto

- dogs-service/
- cats-service/
- docker-compose.yml
- azure-pipelines.yml

## Funcionalidades principales

- CRUD de entidades
- Validación de entrada con @Valid
- Manejo global de excepciones
- Integración con APIs externas
- Comunicación entre microservicios
- Gestión de imágenes
- Dockerización completa

## Autor

Roumen Ivanov Andreev

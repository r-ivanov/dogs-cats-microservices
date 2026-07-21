# Dogs & Cats Microservices

Proyecto de microservicios desarrollado con **Spring Boot 4**, que expone APIs REST para gestionar perros y gatos, incluyendo integración entre servicios, testing completo, dockerización y pipeline CI.

---

## Tecnologías utilizadas

- Java 25
- Spring Boot 4
- Spring Web MVC
- Spring WebFlux (WebClient)
- Spring Data JPA
- H2 Database
- Flyway
- MapStruct
- Java Records
- JUnit 5 / Mockito
- MockMvc
- JaCoCo
- Docker & Docker Compose
- Azure Pipelines
- Maven Multi-Module

---

## Arquitectura

```text
dogs-cats-parent
├── dogs-service
├── cats-service
├── docker-compose.yml
└── azure-pipelines.yml
```

### Parent Maven

Centraliza:
- Configuración Maven común
- Dependency Management
- Versiones compartidas
- JaCoCo
- Compiler Plugin
- Spring Boot Maven Plugin

## Arquitectura de microservicios

### Dogs Service

Responsabilidades:
- CRUD de perros
- Consumo de API externa de chistes
- Exposición de endpoint de chistes para cats-service
- Consumo de cats-service para obtener Pokémons
- Gestión de imágenes

```text
client/
├── JokeApiClient
└── CatsClient

service/
└── DogService
```

### Cats Service

- CRUD de gatos
- Consumo de API externa de pokemons
- Exposición de endpoint de pokemons consumido por dogs-service
- Consumo de dogs-service para obtener chistes
- Gestión de imágenes

```text
client/
├── DogsClient
└── PokemonApiClient

service/
└── CatService
```

## DTOs y mapeo

- Java Records para DTOs inmutables
- MapStruct para conversión entre entidades y DTOs

Ejemplos:
- DogRequest
- DogResponse
- CatRequest
- CatResponse
- JokeResponse
- PokemonResponse
- PokemonApiResponse

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

## Compilación completa

Desde la raíz del proyecto:

```bash
mvn clean install
```

## Ejecución en local

### Dogs Service

```bash
cd dogs-service
mvn spring-boot:run
```

- API: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console

### Cats Service

```bash
cd cats-service
mvn spring-boot:run
```

- API: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui/index.html
- H2 Console: http://localhost:8082/h2-console

## Docker

```bash
mvn clean package
docker compose up --build
```

## Testing

### Proyecto completo

```bash
mvn clean test
```

### Dogs Service

```bash
cd dogs-service
mvn clean test
```

- Swagger: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:dogsdb
- Usuario: sa

### Cats Service

```bash
cd cats-service
mvn clean test
```

- Swagger: http://localhost:8082/swagger-ui/index.html
- H2 Console: http://localhost:8082/h2-console
- JDBC URL: jdbc:h2:mem:catsdb
- Usuario: sa

## Cobertura JaCoCo

### Dogs Service

- Instruction Coverage: 88%
- Branch Coverage: 100%

### Cats Service

- Instruction Coverage: 82%
- Branch Coverage: 100%

Informes:

```text
dogs-service/target/site/jacoco/index.html
cats-service/target/site/jacoco/index.html
```

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

## CI Pipeline

Incluye azure-pipelines.yml para:

- Compilar el proyecto
- Ejecutar tests automáticamente

## Estructura del proyecto

- dogs-service/
- cats-service/
- docker-compose.yml
- azure-pipelines.yml

## Funcionalidades destacadas

- CRUD completo
- Validación con Bean Validation
- Manejo global de excepciones
- Integración entre microservicios
- Consumo de APIs externas
- DTOs con Records
- MapStruct
- Dockerización
- Maven multi-módulo
- Cobertura JaCoCo

## Autor

**Roumen Ivanov Andreev**
